package cs455.scaling.server;

import java.nio.channels.Channel;
import java.util.LinkedList;

public class ChannelBuffer {
  private LinkedList<Channel> channels;

  // May not need these synchronized blocks
  public synchronized Channel get() {
    return channels.poll();
  }
  public synchronized void put(Channel channel) {
    channels.push(channel);
  }
}
