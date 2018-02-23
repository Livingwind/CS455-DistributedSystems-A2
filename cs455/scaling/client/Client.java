package cs455.scaling.client;

import cs455.scaling.exceptions.HashNotFound;
import cs455.scaling.exceptions.SocketClosedException;
import cs455.scaling.utils.ClientStatistics;
import cs455.scaling.utils.HashCalculator;
import cs455.scaling.utils.MessagingConstants;

import javax.swing.plaf.InternalFrameUI;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import static java.nio.channels.SelectionKey.OP_READ;

public class Client {
  public static void main(String[] args) {
    if(args.length != 3) {
      System.out.println("Invalid usage: Not enough arguments." +
          "\nClient server-host server-port message-rate.");
      return;
    }

    InetSocketAddress serverAddr =
        new InetSocketAddress(args[0], Integer.parseInt(args[1]));
    int rate = Integer.parseInt(args[2]);

    Client client = new Client(serverAddr, rate);
    try {
      client.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private InetSocketAddress serverAddr;

  private LinkedBlockingQueue<String> hashes = new LinkedBlockingQueue<>();
  private ClientStatistics stats = new ClientStatistics();
  private SocketChannel channel;
  private int rate;

  public Client(InetSocketAddress serverAddr, int rate) {
    this.serverAddr = serverAddr;
    this.rate = rate;
  }

  private void recvMessage() throws IOException, SocketClosedException{
    ByteBuffer buf = ByteBuffer.allocate(MessagingConstants.HASH_BIT_SIZE);
    int read = 0;
    buf.clear();

    while(buf.hasRemaining() && read != -1) {
      read = channel.read(buf);
    }

    if(read == -1) {
      throw new SocketClosedException();
    }

    String hash = new String(buf.array());

    try {
      removeMessage(hash);
    } catch (HashNotFound hnfe) {
      System.err.println("ERROR: Hash not found in list.");
    }
  }

  private void removeMessage(String hash) throws HashNotFound {
    boolean result;
    synchronized (hashes) {
      result = hashes.remove(hash);
    }

    if (!result) {
      throw new HashNotFound();
    }
  }

  private void startThreads () {
    (new ClientSender(stats, hashes, channel, rate)).start();
    (new ClientThroughputChecker(stats, 5)).start();
  }

  // Main program loop
  public void start() throws IOException, InterruptedException {
    Selector selector = Selector.open();

    channel = SocketChannel.open();
    channel.configureBlocking(false);
    channel.connect(serverAddr);
    System.out.println("Connecting to server...");
    channel.finishConnect();
    System.out.println("Successfully connected.");
    channel.register(selector, OP_READ);

    startThreads();

    try {
      do {
        int ready = selector.select();
        if (ready == 0) {
          continue;
        }
        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
        while (keys.hasNext()) {
          SelectionKey key = keys.next();
          if (key.isReadable()) {
            recvMessage();
          }

          keys.remove();
        }
      } while (true);
    } catch (SocketClosedException sce) {
      System.err.println("TERMINATION: Socket closed connection.");
    }
  }
}
