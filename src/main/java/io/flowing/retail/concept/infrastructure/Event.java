package io.flowing.retail.concept.infrastructure;

import java.util.Map;

public class Event {

  private String eventName;
  private Map<String, Object> payload;

  public Event(String eventName, Map<String, Object> payload) {
    this.eventName = eventName;
    this.payload = payload;
  }

  public String getEventName() {
    return eventName;
  }

  public Map<String, Object> getPayload() {
    return payload;
  }

  public boolean is(String eventName) {
    return this.eventName.equals(eventName);
  }

}
