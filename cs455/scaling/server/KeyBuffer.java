package cs455.scaling.server;

import java.nio.channels.SelectionKey;
import java.util.LinkedList;

public class KeyBuffer {
  private LinkedList<SelectionKey> keys =
      new LinkedList<>();

  public synchronized SelectionKey get() {
    return keys.poll();
  }
  public synchronized void put(SelectionKey key) {
    keys.add(key);
  }
}
