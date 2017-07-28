package io.flowing.retail.concept.domain;

import java.util.Map;

import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.BusObserver;
import io.flowing.retail.concept.infrastructure.Event;

public class Payment implements BusObserver {

  public static void init() {
    Bus.register(new Payment());
  }

  public void eventReceived(Event event) {
    if (event.is("OrderPlaced")) {
      retrievePayment(event.getPayload());
    } // otherwise we do not care here
  }

  public void retrievePayment(Map<String, Object> payload) {
    System.out.println("retrieve payment");
    Bus.send(new Event("PaymentReceived", payload));
  }

}
