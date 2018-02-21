package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;

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
  private int rate;
  private Random rand = new Random();
  private ByteBuffer buf = ByteBuffer.allocate(8000);


  public Client(InetSocketAddress serverAddr, int rate) {
    this.serverAddr = serverAddr;
    this.rate = rate;
  }

  private void sendMessage (SocketChannel channel) throws IOException{
    System.out.println("Sending...");
    buf.clear();
    byte[] bytes = new byte[8000];
    rand.nextBytes(bytes);
    buf.put(bytes);
    buf.flip();

    while (buf.hasRemaining()) {
      channel.write(buf);
    }
    System.out.println(buf.toString());
  }

  // Main program loop
  public void start() throws IOException, InterruptedException {
    SocketChannel channel = SocketChannel.open();
    channel.configureBlocking(false);
    channel.connect(serverAddr);
    System.out.println("Connecting to server...");
    channel.finishConnect();
    System.out.println("Successfully connected.");

    do {
      sendMessage(channel);
      Thread.sleep(1000/rate);
    } while (true);
}
}
