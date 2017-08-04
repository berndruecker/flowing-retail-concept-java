package io.flowing.retail.concept.domain;

import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.BusObserver;
import io.flowing.retail.concept.infrastructure.Event;

public class Order implements BusObserver {

  public static void init() {
    Bus.register(new Order());
  }

  /**
   * Reasons for state handling
   * - Monitoring
   * - Timeout / Escalation
   * - Parallel Processing / Merging
   * - Auditing
   */
  
  public void eventReceived(Event event) {
    if (event.is("OrderPlaced")) {
      Bus.send(new Event("RetrievePaymentCommand", event.getPayload()));
      // now we need to persist some data, as we do not send everything along to payment
      
      // we might also want to watch the latest status / check for timeout. What if payment does not return?
      
      // you definitely need to keep track of the state if you have loops (?)
    }
    if (event.is("PaymentReceived")) {
      Bus.send(new Event("FetchGoodsCommand", event.getPayload()));
      
      // we need to wait for failure messages to trigger compensation of payment
    }
    if (event.is("GoodsFetched")) {
      Bus.send(new Event("ShipGoodsCommand", event.getPayload()));
    }
  }

}
