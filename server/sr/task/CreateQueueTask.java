package sr.task;

import cn.answer.Answer;
import cn.answer.ErrorAnswer;
import cn.answer.SuccessAnswer;
import cn.command.CreateQueueCommand;
import sr.context.AppContext;
import sr.dao.QueueDao;
import cn.model.Queue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task - create queue
 */
public class CreateQueueTask implements Runnable{

  private static final Logger logger = Logger.getLogger(CreateQueueTask.class.getName());

  private CreateQueueCommand command;

  private final SocketChannel clientChannel;

  public CreateQueueTask(CreateQueueCommand command, SocketChannel clientChannel) {
    this.command = command;
    this.clientChannel = clientChannel;
  }

  @Override
  public void run() {

    long startExecTime = System.currentTimeMillis();
    try{
     Queue queue = new Queue();
      queue.setId(command.getQueueId());

      QueueDao dao = getQueueDao();

      long startExecSqlTime = System.currentTimeMillis();
      dao.save(queue);
      long endExecSqlTime = System.currentTimeMillis() - startExecSqlTime;

      logger.info("Created: "+ queue);

      synchronized (clientChannel){
        SuccessAnswer answer = new SuccessAnswer("The queue created");
        setTimeToAnswer(answer, startExecTime, endExecSqlTime);
        clientChannel.write(ByteBuffer.wrap(
                answer.toString().getBytes()));

      }
    } catch (Exception e){
      logger.log(Level.SEVERE, "Error create Queue Task", e);

      try {
        synchronized (clientChannel){
          ErrorAnswer answer = new ErrorAnswer("The queue not created");
          setTimeToAnswer(answer, startExecTime, 0);
          clientChannel.write(ByteBuffer.wrap(
                  answer.toString().getBytes()));
        }
      } catch (IOException e1) {
        logger.log(Level.SEVERE, "Error answer not sand", e1);
      }
    }
  }

  private void setTimeToAnswer(Answer answer, long startExecTime, long endExecSqlTime){
    answer.setDateSend(command.getDateSend());
    answer.setTimeOfReceiptServer(command.getDateRecipient()-command.getDateSend());
    answer.setTimeOfExecSql(endExecSqlTime);
    answer.setTimeOfExecuteServer(System.currentTimeMillis() - startExecTime);
    answer.setDateAnswer(System.currentTimeMillis());
  }

  private QueueDao getQueueDao(){
    return AppContext.getAppContext().getQueueDao();
  }


}
