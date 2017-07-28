package io.flowing.retail.concept.domain;

import io.flowing.retail.concept.infrastructure.Bus;
import io.flowing.retail.concept.infrastructure.BusObserver;
import io.flowing.retail.concept.infrastructure.Event;

public class Order implements BusObserver {

  public static void init() {
    Bus.register(new Order());
  }

  public void eventReceived(Event event) {
    if (event.is("OrderPlaced") && !(Boolean)event.getPayload().get("vip")) {
      Bus.send(new Event("RetrievePaymentCommand", event.getPayload()));
    }
    if (event.is("OrderPlaced") && (Boolean)event.getPayload().get("vip")) {
      Bus.send(new Event("FetchGoodsCommand", event.getPayload()));
    }
    if (event.is("PaymentReceived")) {
      Bus.send(new Event("FetchGoodsCommand", event.getPayload()));
    }
    if (event.is("GoodsFetched")) {
      Bus.send(new Event("ShipGoodsCommand", event.getPayload()));
    }
  }

}
