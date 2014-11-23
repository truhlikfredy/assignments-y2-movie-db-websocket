package eu.antonkrug;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TreeMapExample {
  public static void main(String[] args) {
    Map<Integer, String> map = new TreeMap<Integer, String>();

    // Add Items to the TreeMap
    map.put(new Integer(5), "Five");
    map.put(new Integer(1), "One");
    map.put(new Integer(2), "Two");
    map.put(new Integer(3), "Three");
    map.put(new Integer(4), "Four");
    map.put(new Integer(6), "Six");
    map.put(new Integer(7), "Seven");
    map.put(new Integer(8), "Eight");
    map.put(new Integer(9), "Nine");
    map.put(new Integer(10), "Ten");

    System.out.println(map.get(1));
    // Use iterator to display the keys and associated values
    System.out.println("Map Values Before: ");
    Set keys = map.keySet();
    for (Iterator i = keys.iterator(); i.hasNext();) {
      Integer key = (Integer) i.next();
      String value = (String) map.get(key);
      System.out.println(key + " = " + value);
    }

    // Remove the entry with key 6
    System.out.println("\nRemove element with key 6");
    map.remove(new Integer(6));

    // Use iterator to display the keys and associated values
    System.out.println("\nMap Values After: ");
    keys = map.keySet();
    for (Iterator i = keys.iterator(); i.hasNext();) {
      Integer key = (Integer) i.next();
      String value = (String) map.get(key);
      System.out.println(key + " = " + value);
    }
    
    System.out.println(map.get(0));
    System.out.println(map.get(1));
    System.out.println(map.get(0));
    
  }
}