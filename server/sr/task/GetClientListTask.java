package sr.task;

import cn.answer.Answer;
import cn.answer.ClientListAnswer;
import cn.answer.ErrorAnswer;
import cn.command.ClientListCommand;
import cn.model.Client;
import sr.context.AppContext;
import sr.dao.ClientDao;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetClientListTask implements Runnable{

  private static final Logger logger = Logger.getLogger(GetClientListTask.class.getName());

  private final SocketChannel clientChannel;

  private ClientListCommand command;

  private long startExecTime;

  public GetClientListTask(ClientListCommand command, SocketChannel clientChannel) {
    startExecTime = System.currentTimeMillis();
    this.clientChannel = clientChannel;
    this.command = command;
  }

  @Override
  public void run() {

    try{
      List<Client> clients = getClientDao().getAll();
      long endExecSqlTime = System.currentTimeMillis() - startExecTime;
      synchronized (clientChannel){
        ClientListAnswer answer = new ClientListAnswer();
        answer.setClients(clients);
        answer.setCommandSetId(command.getCommandSetId());
        Answer.setTimeServerToAnswer(command, answer, startExecTime, endExecSqlTime);
        clientChannel.write(ByteBuffer.wrap(
                answer.toString()
                        .getBytes()
        ));
        logger.fine("Send list of clients");
      }
    } catch (Exception e){
      logger.log(Level.SEVERE, "Error get list of clients." + (command != null ? " commandId: "+command.getCommandId(): ""), e);
      try {
        synchronized (clientChannel) {
          ErrorAnswer answer = new ErrorAnswer("Error get list of clients");
          answer.setCommandSetId(command.getCommandSetId());
          Answer.setTimeServerToAnswer(command, answer, startExecTime, 0);
          clientChannel.write(ByteBuffer.wrap(
                  new ErrorAnswer().toString().getBytes()));
        }
      } catch (IOException e1) {
        logger.log(Level.SEVERE, "Error answer not sand."+ (command != null ? " commandId: "+command.getCommandId(): ""), e1);
      }
    }
  }

  private ClientDao getClientDao(){
    return AppContext.getAppContext().getClientDao();
  }

}
