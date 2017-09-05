package io.flowing.retail.concept.domain.prepared;

import java.util.Date;

@SuppressWarnings("unused")
public class OrderEntity {
  
  private String id;

  private String orderContent; 
  
  
  
  public enum OrderState {PLACED, PAYED, FETCHED, SHIPPED, CANCELED};
  private OrderState state = OrderState.PLACED;
  
  
  
  private Date paymentReceived = null;
  private Date goodFetchedDate = null;
  private Date goodShippedDate = null;
  
}
