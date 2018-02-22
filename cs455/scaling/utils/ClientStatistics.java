package cs455.scaling.utils;

public class ClientStatistics {
  Object sLock = new Object();
  Object rLock = new Object();

  private int sent = 0;
  private int recv = 0;

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
  public void incrRecv() {
    synchronized (rLock) {
      recv++;
    }
  }
  public int getRecv() {
    int temp;
    synchronized (rLock) {
      temp = recv;
    }
    return temp;
  }
  public void clear() {
    sent = 0;
    recv = 0;
  }
}
