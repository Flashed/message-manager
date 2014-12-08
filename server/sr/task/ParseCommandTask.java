package sr.task;

import cn.command.*;
import sr.context.AppContext;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ParseCommandTask implements Runnable {

  private static final Logger logger = Logger.getLogger(ParseCommandTask.class.getName());

  private SocketChannel clientChanel;

  private long startExecTime;

  private String commandString;

  public ParseCommandTask(String command, SocketChannel clientChanel, long startExecTime) {
    this.clientChanel = clientChanel;
    this.startExecTime = startExecTime;
    this.commandString = command;
  }

  @Override
  public void run() {
    try {
      Command command = parseCommand(commandString);
      if(command == null){
          logger.severe("Failed to parse command: \n" + commandString);
        return;
      }
      if(logger.isLoggable(Level.FINE)){
        logger.fine("Parsed command " + command.getType());
      }
      if(command instanceof CreateQueueCommand){
        getExecutor().execute(new CreateQueueTask((CreateQueueCommand) command, startExecTime, clientChanel));
      } else if(command instanceof  QueueListCommand){
        getExecutor().execute(new GetQueueListTask((QueueListCommand) command, startExecTime, clientChanel));
      } else if(command instanceof ClientListCommand){
        getExecutor().execute(new GetClientListTask((ClientListCommand) command, startExecTime, clientChanel));
      } else if(command instanceof RegisterClientCommand){
        getExecutor().execute(new RegisterClientTask((RegisterClientCommand) command, startExecTime, clientChanel));
      } else if(command instanceof  SendMessageCommand){
        getExecutor().execute(new SendMessageTask((SendMessageCommand) command, startExecTime, clientChanel));
      } else if(command instanceof  GetMeMessageCommand){
        getExecutor().execute(new GetMessageTask((GetMeMessageCommand) command, startExecTime, clientChanel));
      }
      if(logger.isLoggable(Level.FINE)){
        logger.fine("Size of queue of tasks: " + getExecutor().getQueue().size());
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to parse command: \n" + commandString , e);
    }
    if(logger.isLoggable(Level.FINE)){
      logger.fine(getClass().getName() + " finished in thread: "+ Thread.currentThread().getName());
    }
  }

  private Command parseCommand(String cmd){
    String type = cmd.substring(cmd.indexOf("<type>") + 6);
    type = type.substring(0, type.indexOf("</type>"));

    if(Command.CREATE_QUEUE.equals(type)){
      return parseCreateQueueCommand(cmd);
    } else if (Command.QUEUE_LIST.equals(type)){
      return parseQueueListCommand(cmd);
    } else if (Command.CLIENT_LIST.equals(type)){
      return parseClientListCommand(cmd);
    } else if (Command.REGISTER_CLIENT.equals(type)){
      return parseRegisterClientCommand(cmd);
    } else if (Command.SEND_MESSAGE.equals(type)){
      return parseSendMessageCommand(cmd);
    }  else if (Command.GET_ME_MESSAGE.equals(type)){
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
      logger.log(Level.SEVERE, "Error parse GetMeMessageCommand: \n" + commandString , e);
    }
    return null;
  }

  private QueueListCommand parseQueueListCommand(String data){
    try{
      QueueListCommand command = new QueueListCommand();
      parseCommonFields(command, data);
      return command;
    } catch (Exception e){
      logger.log(Level.SEVERE, "Error parse QueueListCommand: \n" + commandString , e);
    }
    return null;
  }

  private ClientListCommand parseClientListCommand(String data){
    try{
      ClientListCommand command = new ClientListCommand();
      parseCommonFields(command, data);
      return command;
    } catch (Exception e){
      logger.log(Level.SEVERE, "Error parse ClientListCommand: \n" + commandString , e);
    }
    return null;
  }

  private RegisterClientCommand parseRegisterClientCommand(String data){
    try{
      RegisterClientCommand command = new RegisterClientCommand();
      parseCommonFields(command, data);
      return command;
    } catch (Exception e){
      logger.log(Level.SEVERE, "Error parse RegisterClientCommand: \n" + commandString , e);
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
      logger.log(Level.SEVERE, "Error parse CreateQueueCommand: \n" + commandString , e);
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
      logger.log(Level.SEVERE, "Error parse CreateQueueCommand: \n" + commandString , e);
    }
    return null;
  }

  private void parseCommonFields(Command command, String data){
    String clientId = data.substring(data.indexOf("<clientId>") + 10);
    clientId = clientId.substring(0, clientId.indexOf("</clientId>"));
    String commandId = data.substring(data.indexOf("<commandId>") + 11);
    commandId = commandId.substring(0, commandId.indexOf("</commandId>"));
    String dateSend = data.substring(data.indexOf("<dateSend>") + 10);
    dateSend = dateSend.substring(0, dateSend.indexOf("</dateSend>"));
    String commandSetId = data.substring(data.indexOf("<commandSetId>") + 14);
    commandSetId = commandSetId.substring(0, commandSetId.indexOf("</commandSetId>"));

    command.setClientId(Integer.valueOf(clientId));
    command.setCommandId(Long.valueOf(commandId));
    command.setDateSend(Long.valueOf(dateSend));
    command.setCommandSetId(Integer.valueOf(commandSetId));
  }

  private ThreadPoolExecutor getExecutor(){
    return AppContext.getAppContext().getPoolExecutor();
  }

}
