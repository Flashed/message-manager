package sr.task;

import sr.command.Command;
import sr.command.CreateQueueCommand;
import sr.context.AppContext;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Mikhail Zaitsev
 */
public class ParseTask implements Runnable {

  private ByteBuffer readBuffer;

  private SocketChannel clientChanel;

  public ParseTask(ByteBuffer readBuffer, SocketChannel clientChanel) {
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
          Command command = parseCommand(buffer);
          if(command instanceof CreateQueueCommand){
            getExecutor().execute(new CreateQueueTask((CreateQueueCommand) command));
          }
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
      if(data.contains("<type>") && data.contains("</type>")){
        return true;
      }
    }
    return false;
  }

  private Command parseCommand(StringBuilder buffer){
    String data = buffer.toString();
    data = data.substring(data.indexOf("<cmd>") + 5);
    data = data.substring(0, data.indexOf("</cmd>"));
    String type = data.substring(data.indexOf("<type>") + 6);
    type = type.substring(0, type.indexOf("</type>"));

    if(Command.CREATE_QUEUE.equals(type)){
      return parseCreateQueueCommand(data);
    }

    return null;
  }

  private CreateQueueCommand parseCreateQueueCommand(String data){
    try{

      String clientId = data.substring(data.indexOf("<clientId>") + 10);
      clientId = clientId.substring(0, clientId.indexOf("</clientId>"));
      String queueId = data.substring(data.indexOf("<queueId>") + 9);
      queueId = queueId.substring(0, queueId.indexOf("</queueId>"));

      CreateQueueCommand command = new CreateQueueCommand();
      command.setType(Command.CREATE_QUEUE);
      command.setClientId(Integer.valueOf(clientId));
      command.setQueueId(Integer.valueOf(queueId));

      return command;

    }catch (Exception ignore){}
    return null;
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

  private ThreadPoolExecutor getExecutor(){
    return AppContext.getAppContext().getPoolExecutor();
  }

}
