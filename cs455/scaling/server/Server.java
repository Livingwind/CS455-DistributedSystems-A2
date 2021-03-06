package cs455.scaling.server;

import cs455.scaling.utils.ServerStatistics;

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
      System.err.println("ERROR: Could not start.");
      ioe.printStackTrace();
    }
  }

  private ConnectionProcessor connectionProcessor;
  private Thread processorThread;
  private ThreadPoolManager threadManager;
  private Thread poolThread;
  private ServerThroughputChecker checker;
  private Thread checkerThread;

  public Server(int portnum, int poolsize) throws IOException {
    KeyBuffer buff = new KeyBuffer();
    ServerStatistics stats = new ServerStatistics();

    connectionProcessor = new ConnectionProcessor(portnum, buff, stats);
    threadManager = new ThreadPoolManager(poolsize, buff, stats);
    checker = new ServerThroughputChecker(stats, 20);
  }

  // Main program loop
  public void start() {
    processorThread = new Thread(connectionProcessor);
    poolThread = new Thread(threadManager);
    checkerThread = new Thread(checker);

    processorThread.start();
    poolThread.start();
    checkerThread.start();
  }
}