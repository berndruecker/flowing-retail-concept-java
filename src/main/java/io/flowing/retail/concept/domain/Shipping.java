package io.flowing.retail.concept.domain;
import java.util.Map;

import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.EventObserver;
import io.flowing.retail.concept.infrastructure.Event;

public class Shipping implements EventObserver {
  
  public static void init() {
    Bus.register(new Shipping());
  }
  
  public void eventReceived(Event event) {
    if (event.is("GoodsFetched")) {
      shipGoods(event.getPayload());
    }  
    // if (event.is("ShipGoodsCommand")) {
    //   shipGoods(event.getPayload());
    // }  
  }
  
  public void shipGoods(Map<String, Object> payload) {
    System.out.println("ship goods");
    Bus.send( new Event("GoodsShipped", payload));
  }

}
