package sr;

import sr.context.AppContext;
import sr.task.TaskEcho;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Mikhail Zaitsev
 */
public class Server {

  private boolean started;

  private int port;

  private Selector selector;

  private ThreadPoolExecutor poolExecutor;

  public Server(int port) {
    this.port = port;
  }

  public void init() throws IOException {

    selector = SelectorProvider.provider().openSelector();

    ServerSocketChannel serverChanel = ServerSocketChannel.open();
    serverChanel.configureBlocking(false);

    InetSocketAddress isa = new InetSocketAddress(port);
    serverChanel.socket().bind(isa);

    serverChanel.register(selector, SelectionKey.OP_ACCEPT);

  }

  public void start(){
    if(started){
      return;
    }
    started = true;

    while (started) {
      try {
        // Wait for an event one of the registered channels
        this.selector.select();

        // Iterate over the set of keys for which events are available
        Iterator selectedKeys = this.selector.selectedKeys().iterator();
        while (selectedKeys.hasNext()) {
          SelectionKey key = (SelectionKey) selectedKeys.next();
          selectedKeys.remove();

          if (!key.isValid()) {
            continue;
          }

          // Check what event is available and deal with it
          if (key.isAcceptable()) {
            accept(key);
          } else if (key.isReadable()) {
            read(key);
          } else{
            System.out.println("Other key type");
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  private void accept(SelectionKey key) throws IOException {
    // For an accept to be pending the channel must be a manager.sr socket channel.
    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

    // Accept the connection and make it non-blocking
    SocketChannel socketChannel = serverSocketChannel.accept();
    Socket socket = socketChannel.socket();
    socketChannel.configureBlocking(false);

    // Register the new SocketChannel with our Selector, indicating
    // we'd like to be notified when there's data waiting to be read
    socketChannel.register(this.selector, SelectionKey.OP_READ);
  }

  private void read(SelectionKey key) throws IOException {
    SocketChannel socketChannel = (SocketChannel) key.channel();

    // Clear out our read buffer so it's ready for new data
    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    readBuffer.clear();

    // Attempt to read off the channel
    int numRead;
    try {
      numRead = socketChannel.read(readBuffer);
    } catch (IOException e) {
      // The remote forcibly closed the connection, cancel
      // the selection key and close the channel.
      key.cancel();
      socketChannel.close();
      return;
    }

    if (numRead == -1) {
      // Remote entity shut the socket down cleanly. Do the
      // same from our end and cancel the channel.
      key.channel().close();
      key.cancel();
      return;
    }

    poolExecutor.execute(new TaskEcho(readBuffer, socketChannel));
  }

  public void stop(){
    started = false;
  }

  public void setPoolExecutor(ThreadPoolExecutor poolExecutor) {
    this.poolExecutor = poolExecutor;
  }

  public static void main(String... args) {
    try {
      System.out.println("Application started.");

      Server server = AppContext.getAppContext().getServer();
      server.init();
      server.start();
    } catch (Exception e){
      e.printStackTrace();
    }


  }


}
