package cs455.scaling.client;

import cs455.scaling.utils.ClientStatistics;
import cs455.scaling.utils.HashCalculator;

import javax.swing.plaf.InternalFrameUI;
import java.io.IOException;
import java.net.InetSocketAddress;
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


  private void recvMessage(SelectionKey key) throws IOException{


    synchronized (hashes) {
      hashes.contains()
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

    do {
      int ready = selector.select();
      if(ready == 0) {
        continue;
      }
      Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
      while (keys.hasNext()) {
        SelectionKey key = keys.next();
        if (key.isReadable()) {
          recvMessage(key);
        }

        keys.remove();
      }
    } while (true);
  }
}
