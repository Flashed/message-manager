package sr.task;

import cn.command.*;
import cn.model.Client;
import sr.context.AppContext;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;


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
        logger.fine("Current buffer state " + buffer);
        while (checkBuffer(buffer)){
          Command command = parseCommand(buffer);
          if(command == null){
            return;
          }
          logger.info("Parsed command " + command.getType());
          command.setDateRecipient(System.currentTimeMillis());
          if(command instanceof CreateQueueCommand){
            getExecutor().execute(new CreateQueueTask((CreateQueueCommand) command, clientChanel));
          } else if(command instanceof  QueueListCommand){
            getExecutor().execute(new GetQueueListTask((QueueListCommand) command, clientChanel));
          } else if(command instanceof ClientListCommand){
            getExecutor().execute(new GetClientListTask((ClientListCommand) command, clientChanel));
          } else if(command instanceof RegisterClientCommand){
            getExecutor().execute(new RegisterClientTask((RegisterClientCommand) command, clientChanel));
          } else if(command instanceof  SendMessageCommand){
            getExecutor().execute(new SendMessageTask((SendMessageCommand) command, clientChanel));
          } else if(command instanceof  GetMeMessageCommand){
            getExecutor().execute(new GetMessageTask((GetMeMessageCommand) command, clientChanel));
          }
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
    int start = data.indexOf("<cmd>");
    int end = data.indexOf("</cmd>");
    String cmd = data.substring(start + 5);
    cmd = cmd.substring(0, cmd.indexOf("</cmd>"));
    String type = cmd.substring(cmd.indexOf("<type>") + 6);
    type = type.substring(0, type.indexOf("</type>"));

    end = end + 6;
    if(Command.CREATE_QUEUE.equals(type)){
      buffer.delete(start, end);
      return parseCreateQueueCommand(cmd);
    } else if (Command.QUEUE_LIST.equals(type)){
      buffer.delete(start, end);
      return parseQueueListCommand(cmd);
    } else if (Command.CLIENT_LIST.equals(type)){
      buffer.delete(start, end);
      return parseClientListCommand(cmd);
    } else if (Command.REGISTER_CLIENT.equals(type)){
      buffer.delete(start, end);
      return parseRegisterClientCommand(cmd);
    } else if (Command.SEND_MESSAGE.equals(type)){
      buffer.delete(start, end);
      return parseSendMessageCommand(cmd);
    }  else if (Command.GET_ME_MESSAGE.equals(type)){
      buffer.delete(start, end);
      return parseGetMeMessageCommand(cmd);
    }
    return null;
  }

  private GetMeMessageCommand parseGetMeMessageCommand(String data){
    try{

      String queueId = data.substring(data.indexOf("<queueId>") + 9);
      queueId = queueId.substring(0, queueId.indexOf("</queueId>"));
      String senderId = data.substring(data.indexOf("<senderId>") + 10);
      senderId = senderId.substring(0, senderId.indexOf("</senderId>"));
      String delete = data.substring(data.indexOf("<delete>") + 8);
      delete = delete.substring(0, delete.indexOf("</delete>"));

      GetMeMessageCommand command = new GetMeMessageCommand();
      command.setType(Command.GET_ME_MESSAGE);
      command.setQueueId(Integer.valueOf(queueId));
      command.setDelete(Boolean.valueOf(delete));
      command.setSenderId(Integer.valueOf(senderId));

      parseCommonFields(command, data);

      return command;

    }catch (Exception e){
      logger.log(Level.SEVERE, "Error parse GetMeMessageCommand" , e);
    }
    return null;
  }

  private QueueListCommand parseQueueListCommand(String data){
    try{
      QueueListCommand command = new QueueListCommand();
      parseCommonFields(command, data);
      return command;
    } catch (Exception e){
      logger.log(Level.SEVERE, "Error parse QueueListCommand" , e);
    }
    return null;
  }

  private ClientListCommand parseClientListCommand(String data){
    try{
      ClientListCommand command = new ClientListCommand();
      parseCommonFields(command, data);
      return command;
    } catch (Exception e){
      logger.log(Level.SEVERE, "Error parse ClientListCommand" , e);
    }
    return null;
  }

  private RegisterClientCommand parseRegisterClientCommand(String data){
    try{
      RegisterClientCommand command = new RegisterClientCommand();
      parseCommonFields(command, data);
      return command;
    } catch (Exception e){
      logger.log(Level.SEVERE, "Error parse RegisterClientCommand" , e);
    }
    return null;
  }

  private CreateQueueCommand parseCreateQueueCommand(String data){
    try{

      String queueId = data.substring(data.indexOf("<queueId>") + 9);
      queueId = queueId.substring(0, queueId.indexOf("</queueId>"));

      CreateQueueCommand command = new CreateQueueCommand();
      command.setType(Command.CREATE_QUEUE);
      command.setQueueId(Integer.valueOf(queueId));

      parseCommonFields(command, data);

      return command;

    }catch (Exception e){
      logger.log(Level.SEVERE, "Error parse CreateQueueCommand" , e);
    }
    return null;
  }



  private SendMessageCommand parseSendMessageCommand(String data){
    try{

      String queueId = data.substring(data.indexOf("<queueId>") + 9);
      queueId = queueId.substring(0, queueId.indexOf("</queueId>"));
      String recipientId = data.substring(data.indexOf("<recipientId>") + 13);
      recipientId = recipientId.substring(0, recipientId.indexOf("</recipientId>"));
      String text = data.substring(data.indexOf("<text>") + 6);
      text = text.substring(0, text.indexOf("</text>"));


      SendMessageCommand  command = new SendMessageCommand();
      command.setQueueId(Integer.valueOf(queueId));
      command.setRecipientId(Integer.valueOf(recipientId));
      command.setText(text);

      parseCommonFields(command, data);

      return command;

    }catch (Exception e){
      logger.log(Level.SEVERE, "Error parse CreateQueueCommand" , e);
    }
    return null;
  }

  private void parseCommonFields(Command command, String data){
    String clientId = data.substring(data.indexOf("<clientId>") + 10);
    clientId = clientId.substring(0, clientId.indexOf("</clientId>"));
    String dateSend = data.substring(data.indexOf("<dateSend>") + 10);
    dateSend = dateSend.substring(0, dateSend.indexOf("</dateSend>"));
    String commandSetId = data.substring(data.indexOf("<commandSetId>") + 14);
    commandSetId = commandSetId.substring(0, commandSetId.indexOf("</commandSetId>"));

    command.setClientId(Integer.valueOf(clientId));
    command.setDateSend(Long.valueOf(dateSend));
    command.setCommandSetId(Integer.valueOf(commandSetId));
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
