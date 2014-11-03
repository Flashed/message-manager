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

  public GetMessageTask( GetMeMessageCommand command, SocketChannel clientChannel) {
    this.clientChannel = clientChannel;
    this.command = command;
  }

  @Override
  public void run() {
    long startExecTime = System.currentTimeMillis();
    try{
      MessageDao messageDao = getMessageDao();
      Message message = messageDao.get(command.getQueueId(), command.getClientId());
      long endExecSqlTime = System.currentTimeMillis() - startExecTime;
      if(message != null){
        messageDao.delete(message);
        endExecSqlTime = System.currentTimeMillis() - startExecTime;
        synchronized (clientChannel) {
          MessageAnswer answer = new MessageAnswer();
          answer.setText(message.getText());
          answer.setCommandSetId(command.getCommandSetId());
          answer.setMessageId(message.getId());
          Answer.setTimeToAnswer(command, answer, startExecTime, endExecSqlTime);
          clientChannel.write(ByteBuffer.wrap(
                  answer.toString().getBytes()));
          logger.info("Send " + message);
        }
      } else {
        synchronized (clientChannel) {
          Answer answer = new SuccessAnswer("Messages not found");
          answer.setCommandSetId(command.getCommandSetId());
          Answer.setTimeToAnswer(command, answer, startExecTime, endExecSqlTime);
          clientChannel.write(ByteBuffer.wrap(
                  answer.toString().getBytes()));
        }
      }


    } catch (Exception e){
      logger.log(Level.SEVERE, "Failed to get message", e);
      try {
        synchronized (clientChannel) {
          ErrorAnswer answer = new ErrorAnswer("Failed to get message");
          answer.setCommandSetId(command.getCommandSetId());
          Answer.setTimeToAnswer(command, answer, startExecTime, 0);
          clientChannel.write(ByteBuffer.wrap(
                  answer.toString().getBytes()));
        }
      } catch (IOException e1) {
        logger.log(Level.SEVERE, "Error answer not sand", e1);
      }
    }
  }

  private MessageDao getMessageDao(){
    return AppContext.getAppContext().getMessageDao();
  }
}
