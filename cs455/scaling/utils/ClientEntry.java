package cs455.scaling.utils;

public class ClientEntry {
  private int sent = 0;

  public synchronized void addSent() {
    sent++;
  }
  public int getSent() {
    return sent;
  }
  public void clear() {
    sent = 0;
  }
}
