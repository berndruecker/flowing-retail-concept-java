package io.flowing.retail.concept.domain;
import java.util.Map;

import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.BusObserver;
import io.flowing.retail.concept.infrastructure.Event;

public class Inventory implements BusObserver {
  
  public static void init() {
    Bus.register(new Inventory());
  }

  public void eventReceived(Event event) {
    if (event.is("PaymentReceived")) {
      fetchGoods(event.getPayload());
    } // otherwise we do not care here    
  }
  
  public void fetchGoods(Map<String, Object> payload) {
    System.out.println("fetch goods");
    Bus.send( new Event("GoodsFetched", payload));
  }}
