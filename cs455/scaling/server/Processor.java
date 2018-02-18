package cs455.scaling.server;

import com.sun.org.apache.bcel.internal.generic.Select;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class Processor implements Runnable {
  private ChannelBuffer buffer;
  private Selector selector;

  public Processor(int portnum, ChannelBuffer buffer) throws IOException {
    selector = Selector.open();

    ServerSocketChannel server = ServerSocketChannel.open();
    server.bind(new InetSocketAddress(portnum));
    server.configureBlocking(false);
    server.register(selector, SelectionKey.OP_ACCEPT);

    this.buffer = buffer;
  }

  private void registerChannel(ServerSocketChannel server) {
    try {
      SocketChannel channel = server.accept();
      channel.configureBlocking(false);
      channel.register(selector, SelectionKey.OP_READ);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  private void closeAllConnections() {
    for(SelectionKey key: selector.keys()){
      try {
        key.channel().close();
      } catch (IOException ioe){
        ioe.printStackTrace();
      }
    }
  }

  private void processChannels(Set<SelectionKey> keys) {
    Iterator<SelectionKey> iter = keys.iterator();

    while(iter.hasNext()) {
      SelectionKey key = iter.next();

      if(key.isReadable()) {
        buffer.put(key.channel());
      }
      else if(key.isAcceptable()) {
        registerChannel((ServerSocketChannel) key.channel());
      }

      iter.remove();
    }
  }

  @Override
  public void run() {
    try {
      do {
        int ready = selector.select();
        if(ready == 0) {
          continue;
        }

        processChannels(selector.selectedKeys());
      } while(!Thread.interrupted());
    } catch(IOException ioe) {
      ioe.printStackTrace();
    } finally {
      closeAllConnections();
    }
  }
}
