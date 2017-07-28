package io.flowing.retail.concept.helper;

import java.util.HashMap;
import java.util.Map;

public class Maps {

  public static Map<String, Object> of(String key, Object value) {
    HashMap<String,Object> map = new HashMap<String, Object>();
    map.put(key, value);
    return map;
  }
}
