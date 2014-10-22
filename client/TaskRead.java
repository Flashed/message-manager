import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Mikhail Zaitsev
 */
public class TaskRead implements Runnable{

  private SocketChannel socketChannel;

  private ReadListener readListener;

  private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

  public TaskRead(SocketChannel socketChannel, ReadListener readListener) {
    this.socketChannel = socketChannel;
    this.readListener = readListener;
  }

  @Override
  public void run() {

    if(readListener == null || socketChannel == null){
      return;
    }

    try{

      int num;
      while((num = socketChannel.read(byteBuffer)) != -1){
        readListener.readBByteBuffer(byteBuffer);
      }
      socketChannel.close();
    } catch (Exception e){
      e.printStackTrace();
    }
  }
}
