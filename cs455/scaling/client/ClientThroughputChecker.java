package cs455.scaling.client;

import cs455.scaling.utils.ClientStatistics;

import java.sql.Timestamp;

public class ClientThroughputChecker extends Thread {
  private ClientStatistics stats;
  private int rate;

  ClientThroughputChecker(ClientStatistics stats, int rate) {
    this.stats = stats;
    this.rate = rate;
  }

  private void printStatistics() {
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    int sent = stats.getSent();
    int recv = stats.getRecv();

    String s = String.format("[%s] Total Sent: %d, Total Received: %d",
        ts, sent, recv);

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
    } while(true);
  }
}
