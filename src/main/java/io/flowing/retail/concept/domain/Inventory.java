package io.flowing.retail.concept.domain;

import java.util.Map;

import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.EventObserver;
import io.flowing.retail.concept.infrastructure.Event;

public class Inventory implements EventObserver {

  public static void init() {
    Bus.register(new Inventory());
  }

  public void eventReceived(Event event) {
    if (event.is("PaymentReceived")) {
      fetchGoods(event.getPayload());
    }
    
//    if (event.is("PaymentReceived") && !(Boolean) event.getPayload().get("vip")) {
//      fetchGoods(event.getPayload());
//    }
//    if (event.is("OrderPlaced") && (Boolean) event.getPayload().get("vip")) {
//      fetchGoods(event.getPayload());
//    }

    // if (event.is("FetchGoodsCommand")) {
    //  fetchGoods(event.getPayload());
    // }

  }

  public void fetchGoods(Map<String, Object> payload) {
    // All good:
    System.out.println("fetch goods");
    Bus.send(new Event("GoodsFetched", payload));

    // Boom - goods are not yet found, invetory try to get alternative goods at
    // the right place which takes a while
    // System.out.println("Goods cannot yet be fetched, maybe later!");
  }

}
