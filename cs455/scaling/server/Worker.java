package cs455.scaling.server;

import java.nio.channels.SocketChannel;

public class Worker implements Runnable {
  SocketChannel channel = null;

  private void readBytes() {
    System.out.println("Reading from channel");

    channel = null;
  }

  public synchronized boolean isIdle() {
    return this.channel == null;
  }

  public synchronized void assignWork(SocketChannel channel) {
    System.out.println("Work assigned");
    this.channel = channel;
  }

  @Override
  public void run() {
    do {
      if (this.channel == null) {
        continue;
      }

      readBytes();
    } while (!Thread.interrupted());
  }
}
