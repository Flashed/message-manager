package sr.task;

import sr.context.AppContext;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;
import java.util.Map;

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
      StringBuilder buffer = getBuffer();
      synchronized (buffer){
        writeToCharBuffer(buffer);
        if(checkBuffer(buffer)){
          clientChanel.write(ByteBuffer.wrap(parseCommand(buffer).getBytes()));
          buffer.setLength(0);
        }
      }

      System.out.println(String.format("Executed %s in %s", getClass().getName(), Thread.currentThread().getName()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean checkBuffer(StringBuilder buffer){
    String data = buffer.toString();
    if(data.contains("<cmd>") && data.contains("</cmd>")){
     return true;
    }
    return false;
  }

  private String parseCommand(StringBuilder buffer){
    String data = buffer.toString();
    data = data.substring(data.indexOf("<cmd>") + 5);
    data = data.substring(0, data.indexOf("</cmd>"));
    return data;
  }

  private void writeToCharBuffer(StringBuilder buffer){
    readBuffer.flip();
    byte[] bytes = readBuffer.array();
    for(int i = readBuffer.position(); i<readBuffer.limit(); i++){
      if(buffer.length() > (5*1024)){
        System.out.println("The command > 5KB");
        buffer.setLength(0);
        break;
      }
      buffer.append((char)bytes[i]);
    }
    readBuffer.clear();
  }

  private StringBuilder getBuffer(){
    synchronized (AppContext.getAppContext().getCommandBuffers()){
      if(!AppContext.getAppContext().getCommandBuffers().containsKey(clientChanel)){
        AppContext.getAppContext().getCommandBuffers().put(clientChanel, new StringBuilder());
      }
      return AppContext.getAppContext().getCommandBuffers().get(clientChanel);
    }
  }

}
