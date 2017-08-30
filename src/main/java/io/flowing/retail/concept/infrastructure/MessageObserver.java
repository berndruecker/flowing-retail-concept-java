package io.flowing.retail.concept.infrastructure;

public interface MessageObserver {
  
  void received(Message message);

}
