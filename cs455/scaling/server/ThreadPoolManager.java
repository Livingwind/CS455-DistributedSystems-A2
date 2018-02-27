package cs455.scaling.server;

import cs455.scaling.utils.ServerStatistics;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.LinkedList;

public class ThreadPoolManager implements Runnable {
  private KeyBuffer buff;
  private ArrayList<Worker> allWorkers = new ArrayList<>();
  private LinkedList<Worker> idleWorkers = new LinkedList<>();

  public ThreadPoolManager(int poolsize, KeyBuffer buff,
                           ServerStatistics stats) {
    this.buff = buff;
    for(int i = 0; i < poolsize; i++) {
      allWorkers.add(new Worker(this, stats));
    }
  }

  // Method to add worker to internal ready queue.
  //  This gets called by a Worker to tell the manager it's
  //  ready for another job.
  public void queueWorker(Worker worker) {
    synchronized (idleWorkers) {
      idleWorkers.add(worker);
    }
  }

  private void startWorkers() {
    for(Worker worker: allWorkers) {
      worker.start();
    }
  }

  private void stopWorkers() {
    for(Worker worker: allWorkers) {
      worker.interrupt();
    }

    for(Worker worker: allWorkers) {
      try {
        worker.join(1);
      } catch (InterruptedException ire) {
        ire.printStackTrace();
      }
    }

    System.out.println("TERMINATION: All threads joined.");
  }

  private void assignWorker(SelectionKey key) {
    Worker worker = idleWorkers.poll();
    worker.assignWork(key);
  }

  private void checkForWork() {
    synchronized (idleWorkers) {
      if (idleWorkers.isEmpty()) {
        return;
      }
      SelectionKey key = buff.get();
      if (key == null) {
        return;
      }

      assignWorker(key);
    }
  }

  @Override
  public void run() {
    startWorkers();
    System.out.println("ALERT: Starting ThreadManager");

    do {
      checkForWork();
    } while(!Thread.interrupted());

    stopWorkers();
  }
}
