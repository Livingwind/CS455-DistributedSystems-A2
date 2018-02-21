package cs455.scaling.client;

import java.sql.Timestamp;

public class ClientThroughputChecker implements Runnable {
  private int rate;
  private Client client;

  ClientThroughputChecker(Client client, int rate) {
    this.rate = rate;
    this.client = client;
  }

  private void printStatistics() {
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    int sent = client.sent;
    int recv = client.recv;

    String s = String.format("[%s] Total Sent: %d, Total Received: %d",
        ts, sent, recv);

    System.out.println(s);
    client.clearStatistics();
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
