package io.flowing.retail.concept.infrastructure;

public class Event {

  private String eventName;
  private String payload;

  public Event(String eventName, String payload) {
    this.eventName = eventName;
    this.payload = payload;
  }

  public String getEventName() {
    return eventName;
  }

  public String getPayload() {
    return payload;
  }

  public boolean is(String eventName) {
    return this.eventName.equals(eventName);
  }

}
