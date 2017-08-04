package io.flowing.retail.concept.domain;
import java.util.UUID;

import org.camunda.bpm.engine.variable.Variables;

import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.Event;

public class Shop {
  
  public static void init() {    
  }
  
  public void checkout(boolean vip) {
    String orderId = UUID.randomUUID().toString();
    System.out.println("place order " + orderId);    
    Bus.send( new Event("OrderPlaced", Variables.putValue("orderId", orderId).putValue("vip", vip)));
  }

}
