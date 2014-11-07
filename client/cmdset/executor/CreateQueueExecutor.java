package cmdset.executor;

import cmdset.CommandSet;
import cn.answer.Answer;
import cn.command.Command;
import cn.command.CreateQueueCommand;
import statistic.StatisticService;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Create queue executor
 */
public class CreateQueueExecutor implements CommandSetExecutor{

  private static final Logger logger = Logger.getLogger(CreateQueueExecutor.class.getName());

  private static final String COMMAND_SET_TYPE = CommandSet.TYPE_CREATE_QUEUE;

  private SocketChannel socketChannel;

  private StatisticService statisticService;

  private Map<Long, CommandSetExecutor> handlesTimesExecutorsMap;

  private CommandSetExecutorListener listener;

  public CreateQueueExecutor(SocketChannel socketChannel, StatisticService statisticService) {
    this.socketChannel = socketChannel;
    this.statisticService = statisticService;
  }

  @Override
  public void execute(CommandSet commandSet) {
    try{
      Command command = createCreateQueueCommand(commandSet);
      synchronized (handlesTimesExecutorsMap){
        if(handlesTimesExecutorsMap.containsKey(command.getCommandId())){
          logger.log(Level.SEVERE, "handlesTimesExecutorsMap already contains key");
        }
        handlesTimesExecutorsMap.put(command.getCommandId(), this);
      }
      socketChannel.write(ByteBuffer.wrap(command.toString().getBytes()));
    }catch (Exception e){
      logger.log(Level.SEVERE, "Error send command ", e);
    }
  }

  @Override
  public void handleAnswer(Answer answer) {
    statisticService.write(COMMAND_SET_TYPE, answer);
    if(logger.isLoggable(Level.FINE)){
      logger.fine(getClass().getName() + "finished.");
    }
    listener.onFinished();
  }

  @Override
  public void setHandlesTimesExecutorsMap(Map<Long, CommandSetExecutor> handlesTimesExecutorsMap) {
    this.handlesTimesExecutorsMap = handlesTimesExecutorsMap;
  }

  @Override
  public void setListener(CommandSetExecutorListener listener) {
    this.listener = listener;
  }

  private CreateQueueCommand createCreateQueueCommand(CommandSet commandSet){
    CreateQueueCommand cmd = new CreateQueueCommand();
    cmd.setCommandSetId(commandSet.getId());
    cmd.setQueueId((int)(Math.random()*100000));
    cmd.setCommandId(System.nanoTime());
    cmd.setDateSend(System.currentTimeMillis());
    return cmd;
  }
}
