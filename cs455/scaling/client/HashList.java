package cs455.scaling.client;

import java.util.LinkedList;

public class HashList {
  LinkedList<String> list = new LinkedList<>();

  public synchronized boolean remove(String s) {
    return list.remove(s);
  }
  public synchronized void put(String s) {
    list.add(s);
  }
}
