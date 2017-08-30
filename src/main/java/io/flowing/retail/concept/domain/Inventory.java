package io.flowing.retail.concept.domain;

import java.util.Map;

import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.MessageObserver;
import io.flowing.retail.concept.infrastructure.Message;

public class Inventory implements MessageObserver {

  public static void init() {
    Bus.register(new Inventory());
  }

  public void received(Message message) {  
    if (message.is("FetchGoodsCommand")) {
      fetchGoods(message.getPayload());
    }

  }

  public void fetchGoods(Map<String, Object> payload) {
    // All good:
    System.out.println("fetch goods");
    Bus.send(new Message("GoodsFetched", payload));

    // Boom - goods are not yet found, invetory try to get alternative goods at
    // the right place which takes a while
    // System.out.println("Goods cannot yet be fetched, maybe later!");
  }

}
