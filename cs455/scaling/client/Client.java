package cs455.scaling.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

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
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  private InetSocketAddress serverAddr;
  private int rate;


  public Client(InetSocketAddress serverAddr, int rate) {
    this.serverAddr = serverAddr;
    this.rate = rate;
  }

  // Main program loop
  public void start() throws IOException {
    SocketChannel channel = SocketChannel.open();
    channel.configureBlocking(false);
    channel.connect(serverAddr);
    channel.finishConnect();

    ByteBuffer buf = ByteBuffer.allocate(8000);
    buf.clear();
    buf.put("abc".getBytes());
    buf.flip();

    while(buf.hasRemaining()) {
      channel.write(buf);
    }
  }
}
