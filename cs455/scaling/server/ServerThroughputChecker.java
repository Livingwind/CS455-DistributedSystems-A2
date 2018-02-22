package cs455.scaling.server;

import cs455.scaling.utils.ServerStatistics;

import java.sql.Timestamp;

public class ServerThroughputChecker implements Runnable {
  private int rate;
  private ServerStatistics stats;


  public ServerThroughputChecker(ServerStatistics stats, int rate) {
    this.rate = rate;
    this.stats = stats;
  }

  private void printStatistics() {
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    int sent = stats.getSent();
    int clients = stats.getClients();

    String s = String.format("[%s] Server Throughput: %d messages/s, Active Clients: %d",
        ts, sent/rate, clients);

    System.out.println(s);
    stats.clear();
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
