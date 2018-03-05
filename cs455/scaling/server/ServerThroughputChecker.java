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

  private void printStatistics() throws InterruptedException {
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    AllStatistics all = stats.allStats(rate);

    String s = String.format("[%-23s] Server Throughput: %.2f messages/s, Active Clients: %d,\n" +
        "\tMean Per-client Throughput: %.2f messages/s, Std. Dev. Of Per-client Throughput: %.2f messages/s",
        ts, all.throughput, all.connections,
        all.mean, all.stddev);

    System.out.println(s);
  }

  @Override
  public void run() {
    do {
      try {
        printStatistics();
        Thread.sleep(rate * 1000);
      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
    } while (true);
  }
}
