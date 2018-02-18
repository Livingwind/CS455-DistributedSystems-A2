package cs455.scaling.server;

import java.nio.channels.SocketChannel;

public class Worker implements Runnable {
  SocketChannel channel = null;

  private void readBytes(SocketChannel channel) {
    System.out.println("Reading from channel");

    channel = null;
  }

  public boolean isIdle() {
    return channel == null;
  }

  public synchronized void assignWork(SocketChannel channel) {
    this.channel = channel;
  }

  @Override
  public void run() {
    do {
      if (channel == null) {
        continue;
      }

      readBytes(channel);
    } while (!Thread.interrupted());
  }
}
