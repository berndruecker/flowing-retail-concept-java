package io.flowing.retail.concept.infrastructure;

public interface BusObserver {
  
  void eventReceived(Event event);

}
