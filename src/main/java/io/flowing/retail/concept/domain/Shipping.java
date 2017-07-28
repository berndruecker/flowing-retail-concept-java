package io.flowing.retail.concept.domain;
import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.BusObserver;
import io.flowing.retail.concept.infrastructure.Event;

public class Shipping implements BusObserver {
  
  public static void init() {
    Bus.register(new Shipping());
  }
  
  public void eventReceived(Event event) {
    if (event.is("GoodsFetched")) {
      shipGoods();
    } // otherwise we do not care here    
  }
  
  public void shipGoods() {
    System.out.println("ship goods");
    Bus.send( new Event("GoodsShipped", "{ ...}"));
  }

}
