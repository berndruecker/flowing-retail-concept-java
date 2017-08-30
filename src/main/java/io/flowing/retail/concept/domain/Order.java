package io.flowing.retail.concept.domain;

import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.MessageObserver;
import io.flowing.retail.concept.infrastructure.Message;

public class Order implements MessageObserver {

  public static void init() {
    Bus.register(new Order());
  }
  
  public void received(Message message) {
    if (message.is("OrderPlacedEvent")) {
      Bus.send(new Message("RetrievePaymentCommand", message.getPayload())); 
    }
    if (message.is("PaymentReceivedEvent")) {
      Bus.send(new Message("FetchGoodsCommand", message.getPayload()));      
    }
    if (message.is("GoodsFetchedEvent")) {
      Bus.send(new Message("ShipGoodsCommand", message.getPayload()));
    }
  }

}
