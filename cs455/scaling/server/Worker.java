package cs455.scaling.server;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Worker extends Thread {
  private ThreadPoolManager manager;
  private SelectionKey key = null;
  private ByteBuffer buf = ByteBuffer.allocate(8000);

  public Worker(ThreadPoolManager manager) {
    this.manager = manager;
  }

  public synchronized void assignWork(SelectionKey key) {
    this.key = key;
    notify();
  }
  private synchronized void awaitWork() throws InterruptedException {
    manager.queueWorker(this);
    wait();
  }

  private void readBytes() throws IOException {
    SocketChannel channel = (SocketChannel) key.channel();

    int read = 0;
    buf.clear();

    while (buf.hasRemaining() && read != -1) {
      read = channel.read(buf);
    }

    if (read == -1) {
      System.out.println("CLOSING");
      closeSocket();
    }

    System.out.println("Message from " + ((SocketChannel) key.channel()).getRemoteAddress());
    System.out.println("Parsed by: " + Thread.currentThread());
  }

  private void writeBytes() {

  }

  private void closeSocket() {
    try {
      key.channel().close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }


  @Override
  public void run() {
    try {
      do {
        awaitWork();

        try {
          readBytes();
        } catch (IOException ioe) {
          closeSocket();
        }

        key.cancel();
      } while (!Thread.interrupted());
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    }
  }
}
