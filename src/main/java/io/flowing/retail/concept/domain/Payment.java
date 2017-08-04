package io.flowing.retail.concept.domain;

import java.util.Map;

import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.EventObserver;
import io.flowing.retail.concept.infrastructure.Event;

public class Payment implements EventObserver {

  public static void init() {
    Bus.register(new Payment());
  }

  public void eventReceived(Event event) {
    if (event.is("OrderPlaced")) {
      retrievePayment(event.getPayload());
    }
    // if (event.is("OrderPlaced") && !(Boolean)event.getPayload().get("vip")) {
    //   retrievePayment(event.getPayload());
    // }
    
    // if (event.is("RetrievePaymentCommand")) {
    //   retrievePayment(event.getPayload());
    // }
  }

  public void retrievePayment(Map<String, Object> payload) {
    Bus.send(new Event("PaymentReceived", payload));
  }

}
