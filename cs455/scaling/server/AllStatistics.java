package cs455.scaling.server;

public class AllStatistics {
  public int clients;
  public double throughput;
  public double mean;
  public double stddev;

  public AllStatistics() {}

  public AllStatistics(int c, double t, double m, double s) {
    clients = c;
    throughput = t;
    mean = m;
    stddev = s;
  }
}
