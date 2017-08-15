package io.flowing.retail.concept.domain;

import java.io.File;
import java.sql.SQLException;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.h2.tools.Server;

import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.Event;
import io.flowing.retail.concept.infrastructure.EventObserver;

public class OrderCamunda implements EventObserver {

  /**
   * Reasons for state handling
   * - Monitoring
   * - Timeout / Escalation
   * - Parallel Processing / Merging
   * - Auditing
   */
  private static ProcessEngine camunda;
  
  public static void init() throws SQLException {
    Bus.register(new OrderCamunda());
    
    // Configure Camunda engine (in this case using in memory H2)
    StandaloneInMemProcessEngineConfiguration conf = new StandaloneInMemProcessEngineConfiguration();
    conf.setJobExecutorActivate(true);
    conf.setHistoryLevel(HistoryLevel.HISTORY_LEVEL_FULL);
    conf.setJdbcUsername("sa");
    conf.setJdbcPassword("sa");
    camunda = conf.buildProcessEngine();
    // and start H2 database server to allow inspection from the outside
    Server.createTcpServer(new String[] { "-tcpPort", "8092", "-tcpAllowOthers" }).start();
    
    // Define flow
    BpmnModelInstance flow = extendedFlowOfActivities();
        
    // Deploy finished flow to Camunda
    camunda.getRepositoryService().createDeployment() //
        .addModelInstance("order.bpmn", flow) //
        .deploy();
    
    // Only for demo: write flow to file, so we can open it in modeler
    Bpmn.writeModelToFile(new File("order.bpmn"), flow);
  }

  @SuppressWarnings("unused")
  private static BpmnModelInstance simpleFlowOfActivities() {
    ProcessBuilder flow = Bpmn.createExecutableProcess("order");    
    flow.startEvent()
        .serviceTask().name("Retrieve payment").camundaClass(RetrievePaymentAdapter.class) //
        .receiveTask("waitForPayment").message("PaymentReceived") //
        .serviceTask().name("Fetch goods").camundaClass(FetchGoodsAdapter.class) //
        .receiveTask("waitForGoods").message("GoodsFetched") //
        .serviceTask().name("Ship goods").camundaClass(ShipGoodsAdapter.class) //
        .receiveTask("waitForShipping").message("GoodsShipped") //
        .endEvent(); //
    return flow.done();
  }
  
  private static BpmnModelInstance extendedFlowOfActivities() {
    ProcessBuilder flow = Bpmn.createExecutableProcess("order");
    flow.startEvent()
        .exclusiveGateway("split").condition("normal folks", "#{not vip}") //
          .serviceTask().name("Retrieve payment").camundaClass(RetrievePaymentAdapter.class) //
            .boundaryEvent().compensateEventDefinition().compensateEventDefinitionDone() //
            .compensationStart() //
              .serviceTask().name("refund payment").camundaClass(RefundPaymentAdapter.class) //
            .compensationDone() //
          .receiveTask("waitForPayment").message("PaymentReceived") //
        // This is the point where we join the paths again
        .exclusiveGateway("join")
        .serviceTask().name("Fetch goods").camundaClass(FetchGoodsAdapter.class) //
        .receiveTask("waitForGoods").message("GoodsFetched") //
          // Define some timeout behavior
          .boundaryEvent().timerWithDuration("PT2S") //
             .serviceTask().name("Cancel order").camundaClass(CancelEverythingAdapter.class) //
             .intermediateThrowEvent().compensateEventDefinition().compensateEventDefinitionDone() //
             .endEvent() // 
        // and go on in normal flow
        .moveToNode("waitForGoods")
        .serviceTask().name("Ship goods").camundaClass(ShipGoodsAdapter.class) //
        .receiveTask("waitForShipping").message("GoodsShipped") //
        .endEvent() //
        // Now define the other path, where we don't do the payment
        .moveToNode("split").condition("VIP", "#{vip}").connectTo("join");
    return flow.done();
  }
  
  // Adapter classes doing the real work
  public static class RetrievePaymentAdapter implements JavaDelegate {
    public void execute(DelegateExecution ctx) throws Exception {
      Bus.send(new Event("RetrievePaymentCommand", ctx.getVariables()));      
    }
  }
  public static class RefundPaymentAdapter implements JavaDelegate {
    public void execute(DelegateExecution ctx) throws Exception {
      Bus.send(new Event("RefundPaymentCommand", ctx.getVariables()));      
    }
  }
  public static class FetchGoodsAdapter implements JavaDelegate {
    public void execute(DelegateExecution ctx) throws Exception {
      Bus.send(new Event("FetchGoodsCommand", ctx.getVariables()));      
    }
  }
  public static class ShipGoodsAdapter implements JavaDelegate {
    public void execute(DelegateExecution ctx) throws Exception {
      Bus.send(new Event("ShipGoodsCommand", ctx.getVariables()));      
    }
  }
  public static class CancelEverythingAdapter implements JavaDelegate {
    public void execute(DelegateExecution ctx) throws Exception {
      Bus.send(new Event("OrderCanceledEvent", ctx.getVariables()));      
    }
  }
  
  public void eventReceived(Event event) {
    if (event.is("OrderPlaced")) {
      camunda.getRuntimeService().startProcessInstanceByKey("order", event.getPayload());
      // now we need to persist some data, as we do not send everything along to payment      
      // and you might want to answer questions like:
      // - any order stuck?
      // - how long does a typical order take to be paied, delivered, ...?
      // - at which state do we have how much waiting orders (or how much sales is on the way)
      // - ...
    }
    if (event.is("PaymentReceived")) {
      camunda.getRuntimeService().createMessageCorrelation(event.getEventName()) //
        .processInstanceVariableEquals("orderId", event.getPayload().get("orderId")) //
        .correlateWithResult();      
      // we need to wait for failure messages to trigger compensation of payment
    }
    if (event.is("GoodsFetched")) {
      camunda.getRuntimeService().createMessageCorrelation(event.getEventName()) //
      .processInstanceVariableEquals("orderId", event.getPayload().get("orderId")) //
      .correlateWithResult();      
    }
    if (event.is("GoodsShipped")) {
      camunda.getRuntimeService().createMessageCorrelation(event.getEventName()) //
      .processInstanceVariableEquals("orderId", event.getPayload().get("orderId")) //
      .correlateWithResult();      
    }
  }

}
