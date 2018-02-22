package cs455.scaling.utils;

public class ServerStatistics {
  Object sLock = new Object();
  Object cLock = new Object();

  private int sent;
  private int clients;

  public void incrSent() {
    synchronized (sLock) {
      sent++;
    }
  }
  public int getSent() {
    int temp;
    synchronized (sLock) {
      temp = sent;
    }
    return temp;
  }
  public void clear() {
    sent = 0;
  }

  public void addClients(int x) {
    synchronized (cLock) {
      clients += x;
    }
  }
  public int getClients() {
    int temp;
    synchronized (cLock) {
      temp = clients;
    }
    return temp;
  }

}
