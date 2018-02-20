package cs455.scaling.server;

import java.nio.channels.SelectionKey;
import java.util.concurrent.LinkedBlockingQueue;

public class KeyBuffer {
  private LinkedBlockingQueue<SelectionKey> keys =
      new LinkedBlockingQueue<>();

  public SelectionKey get() {
    return keys.poll();
  }
  public void put(SelectionKey key) {
    keys.add(key);
  }
}
