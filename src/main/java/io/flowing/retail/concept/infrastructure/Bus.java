package io.flowing.retail.concept.infrastructure;
import java.util.ArrayList;
import java.util.List;

public class Bus {
  
  private static List<BusObserver> observers = new ArrayList<BusObserver>();

  public static void send(Event event) {
    System.out.println("-- Event " + event.getEventName() + " --");
    for (BusObserver busObserver : observers) {
      // TODO: make async
      busObserver.eventReceived(event);
    }
  }

  public static void register(BusObserver observer) {
    observers.add(observer);
  }

}
