package sr.task;

import cn.command.Command;
import cn.command.CreateQueueCommand;
import cn.command.QueueListCommand;
import sr.context.AppContext;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mikhail Zaitsev
 */
public class ParseTask implements Runnable {

  private static final Logger logger = Logger.getLogger(ParseTask.class.getName());

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
            getExecutor().execute(new CreateQueueTask((CreateQueueCommand) command, clientChanel));
          } else if(command instanceof  QueueListCommand){
            getExecutor().execute(new GetQueueListTask(clientChanel));
          }
          buffer.setLength(0);
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error parse task" , e);
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
    } else if (Command.QUEUE_LIST.equals(type)){
      return new QueueListCommand();
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

    }catch (Exception e){
      logger.log(Level.SEVERE, "Error parse CreateQueueCommand" , e);
    }
    return null;
  }

  private void writeToCharBuffer(StringBuilder buffer){
    readBuffer.flip();
    byte[] bytes = readBuffer.array();
    for(int i = readBuffer.position(); i<readBuffer.limit(); i++){
      if(buffer.length() > (5*1024)){
        logger.warning("The cn.command > 5KB for " + clientChanel);
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
