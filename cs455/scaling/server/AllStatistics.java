package cs455.scaling.server;

import cs455.scaling.utils.ClientEntry;

import java.util.HashMap;

public class AllStatistics {
  HashMap<Integer, ClientEntry> map;
  double interval;

  public int connections;
  public double throughput;
  public double mean;
  public double stddev;


  public AllStatistics() {}

  public AllStatistics(HashMap<Integer, ClientEntry> map, int interval) {
    this.map = map;
    this.interval = interval;

    connections = map.size();
    throughput = calcThroughput();

    mean = (connections != 0) ? calcMean():0;
    stddev = (connections != 0) ? calcStdDev():0;
  }

  private double calcThroughput() {
    double total = 0;
    for(ClientEntry client: map.values()) {
      total += client.getSent();
    }
    return total/interval;
  }

  private double calcMean() {
    double total = 0;
    for(ClientEntry client: map.values()) {
      total += client.getSent()/interval;
    }
    return total/connections;
  }

  private double calcStdDev() {
    double sum = 0;
    for(ClientEntry client: map.values()) {
      sum += Math.pow(((client.getSent() / interval) - mean), 2);
    }
    return Math.sqrt(sum/connections);
  }
}
