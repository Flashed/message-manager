package cmdset.executor;

import cmdset.CommandSet;
import cn.answer.Answer;
import cn.command.Command;
import cn.command.RegisterClientCommand;
import statistic.StatisticService;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Execute - register client
 */
public class RegisterClientExecutor implements CommandSetExecutor{

  private static final Logger logger = Logger.getLogger(RegisterClientExecutor.class.getName());

  private static final String COMMAND_SET_TYPE = CommandSet.TYPE_REGISTER_CLIENT;

  private SocketChannel socketChannel;

  private StatisticService statisticService;

  private int clientId;

  private Map<Long, CommandSetExecutor> handlesTimesExecutorsMap;

  public RegisterClientExecutor(SocketChannel socketChannel, StatisticService statisticService, int clientId) {
    this.socketChannel = socketChannel;
    this.statisticService = statisticService;
    this.clientId = clientId;
  }

  @Override
  public void execute(CommandSet commandSet) {
    try{
      Command command = createRegisterClientCommand(commandSet);
      synchronized (handlesTimesExecutorsMap){
        if(handlesTimesExecutorsMap.containsKey(command.getCommandId())){
          logger.log(Level.SEVERE, "handlesTimesExecutorsMap already contains key");
        }
        handlesTimesExecutorsMap.put(command.getCommandId(), this);
      }
      socketChannel.write(ByteBuffer.wrap(command.toString().getBytes()));
      logger.info("Send command " + command);
    }catch (Exception e){
      logger.log(Level.SEVERE, "Error send command ", e);
    }
  }

  @Override
  public void handleAnswer(Answer answer) {
    statisticService.write(COMMAND_SET_TYPE, answer);
  }

  @Override
  public void setHandlesTimesExecutorsMap(Map<Long, CommandSetExecutor> handlesTimesExecutorsMap) {
    this.handlesTimesExecutorsMap = handlesTimesExecutorsMap;
  }

  private RegisterClientCommand createRegisterClientCommand(CommandSet commandSet){
    RegisterClientCommand command = new RegisterClientCommand();
    command.setCommandSetId(commandSet.getId());
    command.setClientId(clientId);
    command.setCommandId(System.nanoTime());
    command.setDateSend(System.currentTimeMillis());
    return command;
  }
}
