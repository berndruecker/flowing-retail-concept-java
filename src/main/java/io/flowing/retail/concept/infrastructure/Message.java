package io.flowing.retail.concept.infrastructure;

import java.util.Map;

public class Message {

  private String name;
  private Map<String, Object> payload;

  public Message(String name, Map<String, Object> payload) {
    this.name = name;
    this.payload = payload;
  }

  public String getName() {
    return name;
  }

  public Map<String, Object> getPayload() {
    return payload;
  }

  public boolean is(String messageName) {
    return this.name.equals(messageName);
  }

}
