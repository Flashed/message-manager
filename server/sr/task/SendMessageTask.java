package sr.task;

import cn.answer.Answer;
import cn.answer.ErrorAnswer;
import cn.answer.SuccessAnswer;
import cn.command.SendMessageCommand;
import cn.model.Client;
import cn.model.Message;
import sr.context.AppContext;
import sr.dao.ClientDao;
import sr.dao.MessageDao;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Send Message task
 */
public class SendMessageTask implements Runnable{

  private static final Logger logger = Logger.getLogger(SendMessageTask.class.getName());

  private final SocketChannel clientChannel;

  private SendMessageCommand command;

  public SendMessageTask(SendMessageCommand sendMessageCommand, SocketChannel clientChannel) {
    this.clientChannel = clientChannel;
    this.command = sendMessageCommand;
  }

  @Override
  public void run() {
    long startExecTime = System.currentTimeMillis();
    try {
      MessageDao messageDao = getMessageDao();

      Message message = new Message();
      message.setQueueId(command.getQueueId());
      message.setReceiverId(command.getRecipientId());
      message.setSenderId(command.getClientId());
      message.setText(command.getText());

      long startExecSql = System.currentTimeMillis();
      try {
        messageDao.save(message);
      } catch (Exception ignore) {
      }
      long endExecSqlTime = System.currentTimeMillis() - startExecSql;

      logger.info("Created: " + message);

      synchronized (clientChannel) {
        SuccessAnswer answer = new SuccessAnswer("The message send");
        Answer.setTimeToAnswer(command, answer, startExecTime, endExecSqlTime);
        clientChannel.write(ByteBuffer.wrap(
                answer.toString().getBytes()));
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to send message", e);
      try {
        synchronized (clientChannel) {
          ErrorAnswer answer = new ErrorAnswer("Failed to send message");
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
