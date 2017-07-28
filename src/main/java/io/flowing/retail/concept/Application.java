package io.flowing.retail.concept;

import io.flowing.retail.concept.domain.Inventory;
import io.flowing.retail.concept.domain.Payment;
import io.flowing.retail.concept.domain.Shipping;
import io.flowing.retail.concept.domain.Shop;

public class Application {

  public static void main(String[] args) {
    Payment.init();
    Inventory.init();
    Shipping.init();
    
    Shop shop = new Shop();
    
    System.out.println("##### NO VIP: ");
    shop.checkout(false);
    
    System.out.println("##### VIP: ");
    shop.checkout(true);

  }

}
