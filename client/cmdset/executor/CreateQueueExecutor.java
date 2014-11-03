package cmdset.executor;

import cmdset.CommandSet;
import cn.answer.Answer;
import cn.answer.QueueListAnswer;
import cn.command.Command;
import cn.command.CreateQueueCommand;
import cn.model.Queue;
import statistic.StatisticService;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
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


  public CreateQueueExecutor(SocketChannel socketChannel, StatisticService statisticService) {
    this.socketChannel = socketChannel;
    this.statisticService = statisticService;
  }

  @Override
  public void execute(CommandSet commandSet) {
    try{
      Command command = createCreateQueueCommand(commandSet);
      synchronized (handlesTimesExecutorsMap){
        if(handlesTimesExecutorsMap.containsKey(command.getDateSend())){
          logger.log(Level.SEVERE, "handlesTimesExecutorsMap already contains key");
        }
        handlesTimesExecutorsMap.put(command.getDateSend(), this);
      }
      socketChannel.write(ByteBuffer.wrap(command.toString().getBytes()));
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

  private CreateQueueCommand createCreateQueueCommand(CommandSet commandSet){
    CreateQueueCommand cmd = new CreateQueueCommand();
    cmd.setCommandSetId(commandSet.getId());
    cmd.setQueueId((int)(Math.random()*100000));
    cmd.setDateSend(System.nanoTime());
    return cmd;
  }
}
