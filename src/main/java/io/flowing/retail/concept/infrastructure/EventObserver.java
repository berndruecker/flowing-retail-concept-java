package io.flowing.retail.concept.infrastructure;

public interface EventObserver {
  
  void eventReceived(Event event);

}
