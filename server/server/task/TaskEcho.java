package server.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Mikhail Zaitsev
 */
public class TaskEcho implements Runnable {

  private ByteBuffer readBuffer;

  private SocketChannel clientChanel;

  public TaskEcho(ByteBuffer readBuffer, SocketChannel clientChanel) {
    this.readBuffer = readBuffer;
    this.clientChanel = clientChanel;
  }

  @Override
  public void run() {
    try {
      readBuffer.flip();
      clientChanel.write(readBuffer);
      readBuffer.clear();
      System.out.println(String.format("Executed %s in %s", getClass().getName(), Thread.currentThread().getName()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
