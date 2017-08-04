package io.flowing.retail.concept;

import io.flowing.retail.concept.domain.*;

public class Application {

  public static void main(String[] args) throws Exception {
    Payment.init();
    Inventory.init();
    Shipping.init();
    //Order.init();
    //OrderCamunda.init();
    
    Shop shop = new Shop();
    
    System.out.println("##### NO VIP: ");
    shop.checkout(false);
    
    Thread.sleep(500);
    
    System.out.println("##### VIP: ");
    shop.checkout(true);

    Thread.sleep(10000);
  }

}
