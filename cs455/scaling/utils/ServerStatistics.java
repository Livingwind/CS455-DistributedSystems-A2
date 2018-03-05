package cs455.scaling.utils;

import cs455.scaling.server.AllStatistics;

import java.util.HashMap;

public class ServerStatistics {

  private HashMap<Integer, ClientEntry> clients = new HashMap<>();

  public AllStatistics allStats(int interval) {
    synchronized (clients) {
      AllStatistics stats = new AllStatistics(
      new HashMap<>(clients),
      interval
      );
      clearClients();
      return stats;
    }
  }

  public void incrSent(int hash) {
    synchronized(clients) {
      clients.get(hash).addSent();
    }
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
}
