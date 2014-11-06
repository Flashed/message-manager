package sr.task;

import cn.answer.Answer;
import cn.answer.ErrorAnswer;
import cn.answer.MessageAnswer;
import cn.answer.SuccessAnswer;
import cn.command.GetMeMessageCommand;
import cn.model.Message;
import sr.context.AppContext;
import sr.dao.MessageDao;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task - get message
 */
public class GetMessageTask implements Runnable{

  private static final Logger logger = Logger.getLogger(GetMessageTask.class.getName());

  private GetMeMessageCommand command;

  private final SocketChannel clientChannel;

  private long startExecTime;

  public GetMessageTask( GetMeMessageCommand command, long startExecTime, SocketChannel clientChannel) {
    this.startExecTime = startExecTime;
    this.clientChannel = clientChannel;
    this.command = command;
  }

  @Override
  public void run() {
    try{
      Message message;
      long endExecSqlTime;
      Object guard = getGuard();
      synchronized (guard) {
        MessageDao messageDao = getMessageDao();
        if(command.getSenderId() == -1){
          message = messageDao.last(command.getQueueId(), command.getClientId());
        } else{
          message = messageDao.last(command.getQueueId(), command.getClientId(), command.getSenderId());
        }

        if(message != null && command.isDelete()){
          messageDao.delete(message);
        }
        endExecSqlTime = System.currentTimeMillis() - startExecTime;
      }
      if (message != null) {
          synchronized (clientChannel) {
            MessageAnswer answer = new MessageAnswer();
            answer.setText(message.getText());
            answer.setCommandSetId(command.getCommandSetId());
            answer.setMessageId(message.getId());
            Answer.setTimeServerToAnswer(command, answer, startExecTime, endExecSqlTime);
            clientChannel.write(ByteBuffer.wrap(
                    answer.toString().getBytes()));
            if(logger.isLoggable(Level.FINE)){
              logger.fine("Send " + message);
            }
          }
        } else {
          synchronized (clientChannel) {
            Answer answer = new SuccessAnswer("Messages not found");
            answer.setCommandSetId(command.getCommandSetId());
            Answer.setTimeServerToAnswer(command, answer, startExecTime, endExecSqlTime);
            clientChannel.write(ByteBuffer.wrap(
                    answer.toString().getBytes()));
        }
      }
    } catch (Exception e){
      logger.log(Level.SEVERE, "Failed to get message." + (command != null ? " commandId: "+command.getCommandId(): ""), e);
      try {
        synchronized (clientChannel) {
          ErrorAnswer answer = new ErrorAnswer("Failed to get message");
          answer.setCommandSetId(command.getCommandSetId());
          Answer.setTimeServerToAnswer(command, answer, startExecTime, 0);
          clientChannel.write(ByteBuffer.wrap(
                  answer.toString().getBytes()));
        }
      } catch (IOException e1) {
        logger.log(Level.SEVERE, "Error answer not sand." + (command != null ? " commandId: "+command.getCommandId(): ""), e1);
      }
    }
  }

  private MessageDao getMessageDao(){
    return AppContext.getAppContext().getMessageDao();
  }

  private Object getGuard(){
    return AppContext.getAppContext().getGetMessageGuard();
  }
}
