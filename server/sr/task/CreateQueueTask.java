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

  private long startExecTime;

  public CreateQueueTask(CreateQueueCommand command, SocketChannel clientChannel) {
    startExecTime = System.currentTimeMillis();
    this.command = command;
    this.clientChannel = clientChannel;
  }

  @Override
  public void run() {

    try{
      Queue queue = new Queue();
      queue.setId(command.getQueueId());

      QueueDao dao = getQueueDao();

      long startExecSqlTime = System.currentTimeMillis();
      dao.save(queue);
      long endExecSqlTime = System.currentTimeMillis() - startExecSqlTime;

      if(logger.isLoggable(Level.FINE)){
        logger.fine("Created: "+ queue);
      }


      synchronized (clientChannel){
        SuccessAnswer answer = new SuccessAnswer("The queue created");
        answer.setCommandSetId(command.getCommandSetId());
        Answer.setTimeServerToAnswer(command, answer, startExecTime, endExecSqlTime);
        clientChannel.write(ByteBuffer.wrap(
                answer.toString().getBytes()));

      }
    } catch (Exception e){
      logger.log(Level.SEVERE, "Error create Queue Task." + (command != null ? " commandId: "+command.getCommandId(): ""), e);

      try {
        synchronized (clientChannel){
          ErrorAnswer answer = new ErrorAnswer("The queue not created");
          answer.setCommandSetId(command.getCommandSetId());
          Answer.setTimeServerToAnswer(command, answer, startExecTime, 0);
          clientChannel.write(ByteBuffer.wrap(
                  answer.toString().getBytes()));
        }
      } catch (IOException e1) {
        logger.log(Level.SEVERE, "Error answer not sand" + (command != null ? " commandId: "+command.getCommandId(): "") , e1);
      }
    }
  }

  private QueueDao getQueueDao(){
    return AppContext.getAppContext().getQueueDao();
  }


}
