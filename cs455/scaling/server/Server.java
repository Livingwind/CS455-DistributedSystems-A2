package cs455.scaling.server;

import java.io.IOException;

public class Server {
  public static void main(String[] args) {
    if(args.length != 2) {
      System.out.println("Invalid usage: Not enough arguments." +
          "\nServer portnum thread-pool-size.");
      return;
    }

    int portnum = Integer.parseInt(args[0]);
    int poolsize = Integer.parseInt(args[1]);

    try {
      Server server = new Server(portnum, poolsize);
      server.start();
    } catch(IOException ioe) {
      ioe.printStackTrace();
    }
  }

  private Processor processor;
  private ThreadPoolManager threadManager;

  public Server(int portnum, int poolsize) throws IOException {
    ChannelBuffer buff = new ChannelBuffer();

    processor = new Processor(portnum, buff);
    threadManager = new ThreadPoolManager(poolsize, buff);
  }

  // Main program loop
  public void start() {
  }
}