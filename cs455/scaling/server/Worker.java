package cs455.scaling.server;

import cs455.scaling.exceptions.SocketClosedException;
import cs455.scaling.utils.HashCalculator;
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
  private ByteBuffer readBuf = ByteBuffer.allocate(MessagingConstants.RAND_DATA_BYTE_SIZE);

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

  private byte[] readBytes() throws IOException, SocketClosedException {
    SocketChannel channel = (SocketChannel) key.channel();

    int read = 0;
    readBuf.clear();

    while (readBuf.hasRemaining() && read != -1) {
      read = channel.read(readBuf);
    }

    if (read == -1) {
      throw new SocketClosedException();
    }

    // Reset the interest to READ since it's set to writer in the processor
    key.interestOps(SelectionKey.OP_READ);
    return readBuf.array();
  }

  private void writeBytes(byte[] bytes) throws IOException {
    SocketChannel channel = (SocketChannel) key.channel();
    String hash = HashCalculator.SHA1FromBytes(bytes);
    int write = 0;
    int bufSize = hash.getBytes().length + Integer.SIZE;
    ByteBuffer writeBuf = ByteBuffer.allocate(bufSize);

    writeBuf.putInt(hash.getBytes().length);
    writeBuf.put(hash.getBytes());
    writeBuf.flip();

    while(writeBuf.hasRemaining() && write != -1) {
      write = channel.write(writeBuf);
    }
  }

  private void closeSocket() {
    SocketChannel channel = (SocketChannel) key.channel();
    try {
      stats.removeClient(channel.getRemoteAddress().hashCode());
      channel.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    key.cancel();
  }

  private void processKey() throws SocketClosedException, IOException {
    byte[] data = readBytes();
    writeBytes(data);

    SocketChannel channel = (SocketChannel) key.channel();
    stats.incrSent(channel.getRemoteAddress().hashCode());
  }

  @Override
  public void run() {
    try {
      do {
        awaitWork();

        try {
          processKey();
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
