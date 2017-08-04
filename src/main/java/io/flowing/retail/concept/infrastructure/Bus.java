package io.flowing.retail.concept.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Bus {

  private static List<EventObserver> observers = new ArrayList<EventObserver>();

  public static void send(Event event) {
    System.out.println("-- Event " + event.getEventName() + " -- " + mapAsString(event.getPayload()));
    // process it asynchronously (as a real bus would do)
    new Thread() {
      public void run() {
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
        }
        for (EventObserver busObserver : observers) {
          busObserver.eventReceived(event);
        }
      }
    }.start();
  }

  public static void register(EventObserver observer) {
    observers.add(observer);
  }

  private static String mapAsString(Map<String, Object> map) {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    for (Entry<String, Object> entry : map.entrySet()) {
      sb.append(entry.getKey() + "=" + entry.getValue()).append(" | ");
    }
    if (sb.length()>=0) {
      sb.delete(sb.length()-3, sb.length());
    }
    sb.append("]");
    return sb.toString();    
  }
}
