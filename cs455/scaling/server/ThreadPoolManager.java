package cs455.scaling.server;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedList;

public class ThreadPoolManager implements Runnable {
  private ChannelBuffer buff;

  private ArrayList<Thread> threads = new ArrayList<>();
  private ArrayList<Worker> workers = new ArrayList<>();
  private LinkedList<Worker> avaliableWorkers = new LinkedList<>();

  public ThreadPoolManager(int poolsize, ChannelBuffer buff) {
    this.buff = buff;

    for(int i = 0; i < poolsize; i++) {
     createWorker();
    }
  }

  // Housekeeping operations
  private void createWorker() {
    Worker worker = new Worker();
    workers.add(worker);
    threads.add(new Thread(new Worker()));
    queueWorker(worker);
  }

  private void startWorkers() {
    for(Thread thread: threads) {
      thread.start();
    }
  }

  private void stopWorkers() {
    for(Thread thread: threads) {
      thread.interrupt();
    }

    for(Thread thread: threads) {
      try {
        thread.join(1);
      } catch (InterruptedException ire) {
        ire.printStackTrace();
      }
    }

    System.out.println("TERMINATION: All threads joined.");
  }

  //
  private void queueWorker(Worker worker) {
    avaliableWorkers.push(worker);
  }

  private void findIdleWorkers() {
    for(Worker worker: workers) {
      if(worker.isIdle()) {
        queueWorker(worker);
      }
    }
  }

  private void assignWorker(SocketChannel channel) {
    Worker worker = avaliableWorkers.pop();
    worker.assignWork(channel);
  }

  private void checkForWork() {
    if(avaliableWorkers.isEmpty()) {
      return;
    }

    SocketChannel channel = buff.get();
    if(channel == null) {
      return;
    }

    System.out.println("GOTMESSAGE");
    assignWorker(channel);
  }

  @Override
  public void run() {
    startWorkers();
    System.out.println("ALERT: Starting ThreadManager with " +
        workers.size() + " workers.");

    do {
      findIdleWorkers();
      checkForWork();
    } while(!Thread.interrupted());

    stopWorkers();
  }
}
