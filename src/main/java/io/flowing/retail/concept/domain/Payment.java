package io.flowing.retail.concept.domain;

import java.util.Map;

import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.MessageObserver;
import io.flowing.retail.concept.infrastructure.Message;

public class Payment implements MessageObserver {

  public static void init() {
    Bus.register(new Payment());
  }

  public void received(Message message) {
    if (message.is("RetrievePaymentCommand")) {
       retrievePayment(message.getPayload());
    }
  }

  public void retrievePayment(Map<String, Object> payload) {
    System.out.println("retrieve payment");    
    Bus.send(new Message("PaymentReceivedEvent", payload));
  }

}
