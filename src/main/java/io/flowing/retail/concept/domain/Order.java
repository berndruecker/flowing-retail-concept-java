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
import io.flowing.retail.concept.infrastructure.Message;
import io.flowing.retail.concept.infrastructure.MessageObserver;

public class Order implements MessageObserver {

  /**
   * Reasons for state handling
   * - Monitoring
   * - Timeout / Escalation
   * - Parallel Processing / Merging
   * - Auditing
   */
  private static ProcessEngine camunda;
  
  public static void init() throws SQLException {
    Bus.register(new Order());
    
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
    flow.startEvent().message("OrderPlacedEvent")
        .serviceTask().name("Retrieve payment").camundaClass(RetrievePaymentAdapter.class) //
        .receiveTask().name("Wait for payment").message("PaymentReceivedEvent") //
        .serviceTask().name("Fetch goods").camundaClass(FetchGoodsAdapter.class) //
        .receiveTask().name("Wait for goods").message("GoodsFetchedEvent") //
        .serviceTask().name("Ship goods").camundaClass(ShipGoodsAdapter.class) //
        .receiveTask().name("Wait for shipping").message("GoodsShippedEvent") //
        .endEvent(); //
    return flow.done();
  }
  
  private static BpmnModelInstance extendedFlowOfActivities() {
    ProcessBuilder flow = Bpmn.createExecutableProcess("order");
    flow.startEvent().message("OrderPlacedEvent")
        .exclusiveGateway("split").condition("normal folks", "#{not vip}") //
          .serviceTask().name("Retrieve payment").camundaClass(RetrievePaymentAdapter.class) //
            .boundaryEvent().compensateEventDefinition().compensateEventDefinitionDone() //
            .compensationStart() //
              .serviceTask().name("refund payment").camundaClass(RefundPaymentAdapter.class) //
            .compensationDone() //
          .receiveTask().name("Wait for payment").message("PaymentReceivedEvent") //
        // This is the point where we join the paths again
        .exclusiveGateway("join")
        .serviceTask().name("Fetch goods").camundaClass(FetchGoodsAdapter.class) //
        .receiveTask("waitForGoods").name("Wait for goods").message("GoodsFetchedEvent") //
          // Define some timeout behavior
          .boundaryEvent().timerWithDuration("PT2S") //
             .serviceTask().name("Cancel order").camundaClass(CancelEverythingAdapter.class) //
             .intermediateThrowEvent().compensateEventDefinition().compensateEventDefinitionDone() //
             .endEvent() // 
        // and go on in normal flow
        .moveToNode("waitForGoods")
        .serviceTask().name("Ship goods").camundaClass(ShipGoodsAdapter.class) //
        .receiveTask().name("Wait for shipping").message("GoodsShippedEvent") //
        .endEvent() //
        // Now define the other path, where we don't do the payment
        .moveToNode("split").condition("VIP", "#{vip}").connectTo("join");
    return flow.done();
  }
  
  // Adapter classes doing the real work
  public static class RetrievePaymentAdapter implements JavaDelegate {
    public void execute(DelegateExecution ctx) throws Exception {
      Bus.send(new Message("RetrievePaymentCommand", ctx.getVariables()));      
    }
  }
  public static class RefundPaymentAdapter implements JavaDelegate {
    public void execute(DelegateExecution ctx) throws Exception {
      Bus.send(new Message("RefundPaymentCommand", ctx.getVariables()));      
    }
  }
  public static class FetchGoodsAdapter implements JavaDelegate {
    public void execute(DelegateExecution ctx) throws Exception {
      Bus.send(new Message("FetchGoodsCommand", ctx.getVariables()));      
    }
  }
  public static class ShipGoodsAdapter implements JavaDelegate {
    public void execute(DelegateExecution ctx) throws Exception {
      Bus.send(new Message("ShipGoodsCommand", ctx.getVariables()));      
    }
  }
  public static class CancelEverythingAdapter implements JavaDelegate {
    public void execute(DelegateExecution ctx) throws Exception {
      Bus.send(new Message("OrderCanceledEvent", ctx.getVariables()));      
    }
  }
  
  public void received(Message message) {
    if (message.is("OrderPlacedEvent")) {
      camunda.getRuntimeService().createMessageCorrelation(message.getName()) //
        .setVariables(message.getPayload()) //
        .correlateWithResult();      
    }
    if (message.is("PaymentReceivedEvent")) {
      camunda.getRuntimeService().createMessageCorrelation(message.getName()) //
        .processInstanceVariableEquals("orderId", message.getPayload().get("orderId")) //
        .correlateWithResult();      
      // we need to wait for failure messages to trigger compensation of payment
    }
    if (message.is("GoodsFetchedEvent")) {
      camunda.getRuntimeService().createMessageCorrelation(message.getName()) //
      .processInstanceVariableEquals("orderId", message.getPayload().get("orderId")) //
      .correlateWithResult();      
    }
    if (message.is("GoodsShippedEvent")) {
      camunda.getRuntimeService().createMessageCorrelation(message.getName()) //
      .processInstanceVariableEquals("orderId", message.getPayload().get("orderId")) //
      .correlateWithResult();      
    }
  }

}
