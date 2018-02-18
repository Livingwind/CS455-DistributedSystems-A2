package cs455.scaling.server;

import java.util.Queue;

public class ThreadPoolManager implements Runnable {
  private Queue<WorkerThread> workers;

  public ThreadPoolManager(int poolsize, ChannelBuffer buff) {
    for(int i = 0; i < poolsize; i++) {
     createWorker();
    }
  }

  private void createWorker() {
    workers.add(new WorkerThread());
  }
  @Override
  public void run() {
  }
}
