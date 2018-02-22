package cs455.scaling.client;

import cs455.scaling.utils.ClientStatistics;
import cs455.scaling.utils.HashCalculator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientSender extends Thread {
  private Random rand = new Random();
  private ByteBuffer buf = ByteBuffer.allocate(8000);

  private LinkedBlockingQueue hashes;
  private ClientStatistics stats;
  private SocketChannel channel;
  private int rate;

  public ClientSender(ClientStatistics stats, LinkedBlockingQueue hashes,
                      SocketChannel channel, int rate) {
    this.hashes = hashes;
    this.stats = stats;
    this.channel = channel;
    this.rate = rate;
  }

  private void calcHash(byte[] bytes) {
    String hash = HashCalculator.SHA1FromBytes(bytes);
    try {
      synchronized (hashes) {
        hashes.put(hash);
      }
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    }
    System.out.println(hash);
  }


  private void sendMessage(SocketChannel channel) throws IOException {
    byte[] bytes = new byte[8000];
    rand.nextBytes(bytes);
    calcHash(bytes);

    buf.clear();
    buf.put(bytes);
    buf.flip();

    while (buf.hasRemaining()) {
      channel.write(buf);
    }
    stats.incrSent();
  }

  @Override
  public void run() {
    try {
      do {
        sendMessage(channel);
        Thread.sleep(1000 / rate);
      } while (true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
