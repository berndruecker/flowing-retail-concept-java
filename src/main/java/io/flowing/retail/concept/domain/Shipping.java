package io.flowing.retail.concept.domain;
import java.util.Map;

import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.MessageObserver;
import io.flowing.retail.concept.infrastructure.Message;

public class Shipping implements MessageObserver {
  
  public static void init() {
    Bus.register(new Shipping());
  }
  
  public void received(Message message) {
    if (message.is("GoodsFetchedEvent")) {
      shipGoods(message.getPayload());
    }  
  }
  
  public void shipGoods(Map<String, Object> payload) {
    System.out.println("ship goods");
    Bus.send( new Message("GoodsShippedEvent", payload));
  }

}
