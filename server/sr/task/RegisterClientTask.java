package sr.task;

import cn.answer.Answer;
import cn.answer.ErrorAnswer;
import cn.answer.SuccessAnswer;
import cn.command.RegisterClientCommand;
import cn.model.Client;
import sr.context.AppContext;
import sr.dao.ClientDao;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task - register client
 */
public class RegisterClientTask implements Runnable {

  private static final Logger logger = Logger.getLogger(RegisterClientTask.class.getName());

  private final SocketChannel clientChannel;

  private RegisterClientCommand command;

  public RegisterClientTask(RegisterClientCommand command, SocketChannel clientChannel) {
    this.clientChannel = clientChannel;
    this.command = command;
  }

  @Override
  public void run() {
    long startExecTime = System.currentTimeMillis();
    try{
      ClientDao dao = getDao();
      Client client = new Client();
      client.setId(command.getClientId());

      long startExecSql = System.currentTimeMillis();
      try{
        dao.save(client);
      } catch (Exception ignore){}
      long endExecSqlTime = System.currentTimeMillis() - startExecSql;

      logger.info("Created: "+ client);

      synchronized (clientChannel){
        SuccessAnswer answer = new SuccessAnswer("The client registered");
        answer.setCommandSetId(command.getCommandSetId());
        Answer.setTimeToAnswer(command, answer, startExecTime, endExecSqlTime);
        clientChannel.write(ByteBuffer.wrap(
                answer.toString().getBytes()));
      }
    } catch (Exception e){
      logger.log(Level.SEVERE,"Failed to register client");
      try {
        synchronized (clientChannel){
          ErrorAnswer answer = new ErrorAnswer("The client not registered");
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

  private ClientDao getDao(){
    return AppContext.getAppContext().getClientDao();
  }
}
