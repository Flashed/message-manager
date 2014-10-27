package sr.task;

import sr.answer.ErrorAnswer;
import sr.answer.SuccessAnswer;
import sr.command.CreateQueueCommand;
import sr.context.AppContext;
import sr.dao.QueueDao;
import sr.model.Queue;

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

  private SocketChannel clientChannel;

  public CreateQueueTask(CreateQueueCommand command, SocketChannel clientChannel) {
    this.command = command;
    this.clientChannel = clientChannel;
  }

  @Override
  public void run() {

    try{

      Queue queue = new Queue();
      queue.setId(command.getQueueId());

      QueueDao dao = getQueueDao();
      dao.save(queue);

      logger.info("Created: "+ queue);

      clientChannel.write(ByteBuffer.wrap(
              new SuccessAnswer("The queue created").toString().getBytes()));

    } catch (Exception e){
      logger.log(Level.SEVERE, "Error create Queue Task", e);

      try {
        clientChannel.write(ByteBuffer.wrap(
                new ErrorAnswer("The queue not created").toString().getBytes()));
      } catch (IOException e1) {
        logger.log(Level.SEVERE, "Error answer not sand", e1);
      }
    }
  }

  private QueueDao getQueueDao(){
    return AppContext.getAppContext().getQueueDao();
  }


}
