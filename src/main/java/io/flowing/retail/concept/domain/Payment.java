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
    if (event.is("RetrievePaymentCommand")) {
        retrievePayment(event.getPayload());
    }
  }

  public void retrievePayment(Map<String, Object> payload) {
    System.out.println("retrieve payment");
    Bus.send(new Event("PaymentReceived", payload));
  }

}
