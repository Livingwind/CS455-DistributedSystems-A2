package cs455.scaling.server;

import java.sql.Timestamp;

public class ServerThroughputChecker implements Runnable {

  private int rate;
  private ThreadPoolManager manager;
  private ConnectionProcessor processor;

  public ServerThroughputChecker(ThreadPoolManager manager, ConnectionProcessor processor, int rate) {
    this.rate = rate;
    this.manager = manager;
    this.processor = processor;
  }

  private void printStatistics() {
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    int sent = manager.getStatistics();
    int clients = processor.getStatistics();

    String s = String.format("[%s] Server Throughput: %d messages/s, Active Clients: %d",
        ts, sent/rate, clients);

    System.out.println(s);
  }

  @Override
  public void run() {
    do {
      printStatistics();

      try {
        Thread.sleep(rate * 1000);
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
    } while (true);
  }
}
