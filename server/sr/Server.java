package sr;

import sr.context.AppContext;
import sr.task.ParseTask;

import java.io.FileInputStream;
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
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * A server class
 */
public class Server {

  private static final Logger logger = Logger.getLogger(Server.class.getName());
  
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
            logger.info("Other type key: " + key.toString());
          }
        }
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Error server work", e);
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

    logger.info("Connected: " + socketChannel);
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

    poolExecutor.execute(new ParseTask(readBuffer, socketChannel));
  }

  public void stop(){
    started = false;
  }

  public void setPoolExecutor(ThreadPoolExecutor poolExecutor) {
    this.poolExecutor = poolExecutor;
  }

  public static void main(String... args) {
    try {

      try {
        LogManager.getLogManager().readConfiguration(Server.class.getClassLoader().getResourceAsStream("logging.properties"));
      } catch (SecurityException | IOException e1) {
        logger.log(Level.SEVERE, "Error config logger", e1);
      }

      logger.info("Application started.");

      Server server = AppContext.getAppContext().getServer();
      server.init();
      server.start();
    } catch (Exception e){
      logger.log(Level.SEVERE, "", e);
    }


  }


}
