package io.flowing.retail.concept.domain;
import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.Event;

public class Shop {
  
  public static void init() {    
  }
  
  public void checkout() {
    System.out.println("place order");
    Bus.send( new Event("OrderPlaced", "{ ...}"));
  }

}
