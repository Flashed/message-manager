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

  public GetQueueListTask(QueueListCommand command ,SocketChannel clientChannel) {
    this.clientChannel = clientChannel;
    this.command = command;
  }

  @Override
  public void run() {

    long startExecTime = System.currentTimeMillis();
    try{
      List<Queue> queues = getQueueDao().getAll();
      long endExecSqlTime = System.currentTimeMillis() - startExecTime;
      synchronized (clientChannel){
        QueueListAnswer queueListAnswer = new QueueListAnswer(queues);
        queueListAnswer.setCommandSetId(command.getCommandSetId());
        Answer.setTimeToAnswer(command, queueListAnswer, startExecTime, endExecSqlTime);
        clientChannel.write(ByteBuffer.wrap(
                queueListAnswer.toString()
                        .getBytes()
        ));
        logger.info("Send list of queues");
      }
    } catch (Exception e){
      logger.log(Level.SEVERE, "Error get list of queues", e);
      try {
        synchronized (clientChannel) {
          ErrorAnswer answer = new ErrorAnswer("Error get list of queues");
          answer.setCommandSetId(command.getCommandSetId());
          Answer.setTimeToAnswer(command, answer, startExecTime, 0);
          clientChannel.write(ByteBuffer.wrap(
                  new ErrorAnswer().toString().getBytes()));
        }
      } catch (IOException e1) {
        logger.log(Level.SEVERE, "Error answer not sand", e1);
      }
    }
  }

  private QueueDao getQueueDao(){
    return AppContext.getAppContext().getQueueDao();
  }

}
