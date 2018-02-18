package cs455.scaling.server;

import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

public class ChannelBuffer {
  private LinkedBlockingQueue<SocketChannel> channels =
      new LinkedBlockingQueue<>();

  // May not need these synchronized blocks
  public SocketChannel get() {
    return channels.poll();
  }
  public void put(SocketChannel channel) {
    channels.add(channel);
  }
}
