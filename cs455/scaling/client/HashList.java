package cs455.scaling.client;

import java.util.ArrayList;

public class HashList {
  ArrayList<String> list = new ArrayList<>();

  public synchronized boolean remove(String s) {
    return list.remove(s);
  }
  public synchronized void put(String s) {
    list.add(s);
  }
}
