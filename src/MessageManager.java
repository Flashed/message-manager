import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

/**
 * @author Mikhail Zaitsev
 */
public class MessageManager {

  private boolean started;

  private int port;

  private ServerSocketChannel serverChanel;

  private Selector selector;

  private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

  public MessageManager(int port) {
    this.port = port;
  }

  public void init() throws IOException {

    selector = SelectorProvider.provider().openSelector();

    serverChanel = ServerSocketChannel.open();
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
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  private void accept(SelectionKey key) throws IOException {
    // For an accept to be pending the channel must be a server socket channel.
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
    this.readBuffer.clear();

    // Attempt to read off the channel
    int numRead;
    try {
      numRead = socketChannel.read(this.readBuffer);
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

    System.out.println(new String(readBuffer.array()));
  }

  public void stop(){
    started = false;
  }



}
