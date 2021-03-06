package sr.task;

import cn.answer.Answer;
import cn.answer.ErrorAnswer;
import cn.answer.QueueListAnswer;
import cn.command.QueueListCommand;
import sr.context.AppContext;
import sr.dao.QueueDao;
import cn.model.Queue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetQueueListTask implements Runnable{

  private static final Logger logger = Logger.getLogger(GetQueueListTask.class.getName());

  private final SocketChannel clientChannel;

  private QueueListCommand command;

  private long startExecTime;

  public GetQueueListTask(QueueListCommand command, long startExecTime, SocketChannel clientChannel) {
    this.startExecTime = startExecTime;
    this.clientChannel = clientChannel;
    this.command = command;
  }

  @Override
  public void run() {
    try{
      List<Queue> queues = getQueueDao().getAll();
      long endExecSqlTime = System.currentTimeMillis() - startExecTime;
      synchronized (clientChannel){
        QueueListAnswer queueListAnswer = new QueueListAnswer(queues);
        queueListAnswer.setCommandSetId(command.getCommandSetId());
        Answer.setTimeServerToAnswer(command, queueListAnswer, startExecTime, endExecSqlTime);
        clientChannel.write(ByteBuffer.wrap(
                queueListAnswer.toString()
                        .getBytes()
        ));
        if(logger.isLoggable(Level.FINE)){
          logger.fine("Send list of queues");
        }
      }
    } catch (Exception e){
      logger.log(Level.SEVERE, "Error get list of queues." + (command != null ? " commandId: "+command.getCommandId(): ""), e);
      try {
        synchronized (clientChannel) {
          ErrorAnswer answer = new ErrorAnswer("Error get list of queues");
          answer.setCommandSetId(command.getCommandSetId());
          Answer.setTimeServerToAnswer(command, answer, startExecTime, 0);
          clientChannel.write(ByteBuffer.wrap(
                  new ErrorAnswer().toString().getBytes()));
        }
      } catch (IOException e1) {
        logger.log(Level.SEVERE, "Error answer not sand." + (command != null ? " commandId: "+command.getCommandId(): ""), e1);
      }
      if(logger.isLoggable(Level.FINE)){
        logger.fine(getClass().getName() + " finished in thread: "+ Thread.currentThread().getName());
      }
    }
  }

  private QueueDao getQueueDao(){
    return AppContext.getAppContext().getQueueDao();
  }

}
