package cs455.scaling.utils;

import cs455.scaling.server.AllStatistics;

import java.util.HashMap;

public class ServerStatistics {

  private HashMap<Integer, ClientEntry> clients = new HashMap<>();

  public AllStatistics allStats(int interval) {
    AllStatistics stats = new AllStatistics();
    synchronized (clients) {
      stats.clients = clients.size();
      stats.throughput = getSent()/interval;
      stats.mean = calcMean(interval);
      stats.stddev = calcStdDev(interval);
      clearClients();
    }
    return stats;
  }

  public double calcMean(int interval) {
    if(clients.isEmpty()) {
      return 0;
    }

    double total = 0;
    double result;
    synchronized (clients) {
      for(ClientEntry client: clients.values()) {
        total += client.getSent()/interval;
      }
      result = total/getNumClients();
    }
    return result;
  }

  public double calcStdDev(int interval) {
    if(clients.isEmpty())
      return 0;

    double mean = calcMean(interval);
    double sum = 0;
    double result;
    synchronized (clients) {
      for(ClientEntry client: clients.values()) {
        sum += Math.pow(((client.getSent()/interval)-mean), 2);
      }
      result = Math.sqrt(sum/getNumClients());
    }
    return result;
  }

  public void incrSent(int hash) {
    synchronized(clients) {
      clients.get(hash).addSent();
    }
  }

  public int getSent() {
    int total = 0;
    synchronized (clients) {
      for(ClientEntry client: clients.values()) {
        total += client.getSent();
      }
    }
    return total;
  }

  public void clearClients() {
    synchronized (clients) {
      for(ClientEntry client: clients.values()) {
        client.clear();
      }
    }
  }

  public void addClient(int hash) {
    synchronized (clients) {
      clients.put(hash, new ClientEntry());
    }
  }

  public void removeClient(int hash) {
    synchronized (clients) {
      clients.remove(hash);
    }
  }

  public int getNumClients() {
    return clients.size();
  }
}
