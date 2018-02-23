package cs455.scaling.server;

import cs455.scaling.exceptions.SocketClosedException;
import cs455.scaling.utils.MessagingConstants;
import cs455.scaling.utils.ServerStatistics;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Worker extends Thread {
  private ServerStatistics stats;
  private ThreadPoolManager manager;
  private SelectionKey key = null;
  private ByteBuffer buf = ByteBuffer.allocate(MessagingConstants.RAND_DATA_BIT_SIZE);

  public Worker(ThreadPoolManager manager, ServerStatistics stats) {
    this.stats = stats;
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

  private void readBytes() throws IOException, SocketClosedException {
    SocketChannel channel = (SocketChannel) key.channel();

    int read = 0;
    buf.clear();

    while (buf.hasRemaining() && read != -1) {
      read = channel.read(buf);
    }

    if (read == -1) {
      throw new SocketClosedException();
    }
  }

  private void writeBytes() {

  }

  private void closeSocket() {
    try {
      key.channel().close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    key.cancel();
    stats.addClients(-1);
  }

  @Override
  public void run() {
    try {
      do {
        awaitWork();

        try {
          readBytes();
          key.interestOps(SelectionKey.OP_READ);
          stats.incrSent();
        } catch (SocketClosedException sce) {
          closeSocket();
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      } while (!Thread.interrupted());
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    }
  }
}
