package sr.task;

import cn.answer.ErrorAnswer;
import cn.answer.QueueListAnswer;
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

  public GetQueueListTask(SocketChannel clientChannel) {
    this.clientChannel = clientChannel;
  }

  @Override
  public void run() {
    try{
      List<Queue> queues = getQueueDao().getAll();
      synchronized (clientChannel){
        clientChannel.write(ByteBuffer.wrap(
                new QueueListAnswer(queues).toString()
                        .getBytes()));
      }
    } catch (Exception e){
      logger.log(Level.SEVERE, "Error get list of queues", e);
      try {
        synchronized (clientChannel) {
          clientChannel.write(ByteBuffer.wrap(
                  new ErrorAnswer("Error get list of queues").toString().getBytes()));
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
