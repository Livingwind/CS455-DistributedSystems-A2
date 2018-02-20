package cs455.scaling.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Worker extends Thread {
  private volatile boolean idle = true;
  private SelectionKey key = null;
  private ByteBuffer buf = ByteBuffer.allocate(8000);

  private void readBytes() throws IOException {
    SocketChannel channel = (SocketChannel) key.channel();

    int read = 0;
    buf.clear();

    while (buf.hasRemaining() && read != -1) {
      read = channel.read(buf);
    }

    if (read == -1) {
      closeSocket();
    }

    System.out.println("MSG: " + new String(buf.array()));
  }

  private void writeBytes() {

  }

  private void closeSocket() {
    System.out.println("Closing socket.");
    try {
      key.channel().close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    clearKey();
  }

  private void clearKey() {

    key.cancel();
    idle = true;
  }

  public boolean isIdle() {
    return idle;
  }

  public synchronized void assignWork(SelectionKey key) {
    this.key = key;
    idle = false;
  }

  @Override
  public void run() {
    do {
      if (idle) {
        continue;
      }

      try {
        readBytes();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }

      clearKey();
    } while (!Thread.interrupted());
  }
}
