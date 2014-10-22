import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Mikhail Zaitsev
 */
public class Client implements  ReadListener{

  private String host;

  private int port;

  private SocketChannel socketChannel;

  public Client(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void connect(){
    try {
      socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
      new Thread(new TaskRead(socketChannel, this))
              .start();
      System.out.println(String.format("Connected to %s:%s",host, port));
      startWriter();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void startWriter(){
    try{
      ByteBuffer buffer = ByteBuffer.allocate(10);
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      String line = null;
      while((line = reader.readLine()) != null){
        buffer.put(line.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
        buffer.clear();
      }
    }catch (Exception e){
      e.printStackTrace();
    }
  }


  @Override
  public void readBByteBuffer(ByteBuffer byteBuffer) {
    System.out.println(byteBuffer.position());
    System.out.println(new String(byteBuffer.array()));
  }


  public static void main(String... args){
    Client client = new Client("localhost", 4463);
    client.connect();
  }
}
