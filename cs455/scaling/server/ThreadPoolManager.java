package cs455.scaling.server;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.LinkedList;

public class ThreadPoolManager implements Runnable {
  private KeyBuffer buff;

  private ArrayList<Worker> busyWorkers = new ArrayList<>();
  private LinkedList<Worker> idleWorkers = new LinkedList<>();

  public ThreadPoolManager(int poolsize, KeyBuffer buff) {
    this.buff = buff;

    for(int i = 0; i < poolsize; i++) {
     createWorker();
    }
  }

  // Housekeeping operations
  private void createWorker() {
    Worker worker = new Worker();
    idleWorkers.add(worker);
    queueWorker(worker);
  }

  private void startWorkers() {
    LinkedList<Worker> allWorkers = new LinkedList<>(idleWorkers);
    allWorkers.addAll(busyWorkers);

    for(Worker worker: allWorkers) {
      worker.start();
    }
  }

  private void stopWorkers() {
    LinkedList<Worker> allWorkers = new LinkedList<>(idleWorkers);
    allWorkers.addAll(busyWorkers);

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

  //
  private void queueWorker(Worker worker) {
    busyWorkers.remove(worker);
    idleWorkers.push(worker);
  }

  private void findIdleWorkers() {
    for(Worker worker: busyWorkers) {
      if(worker.isIdle()) {
        System.out.println("Found idle.");
        queueWorker(worker);
      }
    }
  }

  private void assignWorker(SelectionKey key) {
    Worker worker = idleWorkers.pop();
    busyWorkers.add(worker);
    worker.assignWork(key);
  }

  private void checkForWork() {
    if(idleWorkers.isEmpty()) {
      return;
    }

    SelectionKey key = buff.get();
    if(key == null) {
      return;
    }

    assignWorker(key);
  }

  @Override
  public void run() {
    startWorkers();
    System.out.println("ALERT: Starting ThreadManager with " +
        idleWorkers.size() + " workers.");

    do {
      findIdleWorkers();
      checkForWork();
    } while(!Thread.interrupted());

    stopWorkers();
  }
}
