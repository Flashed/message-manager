package cmdset.executor;

import cmdset.CommandSet;
import cn.answer.Answer;
import cn.answer.QueueListAnswer;
import cn.command.Command;
import cn.command.QueueListCommand;
import cn.model.Queue;
import statistic.StatisticService;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

enum CountQueuesMode{MODE_ONE,MODE_SEVERAL}
enum SizeMessageMode{MODE_SMALL,MODE_BIG}

/**
 * Sends a broadcast messages
 */
public class SendBroadcastExecutor implements CommandSetExecutor{

  private static final Logger logger = Logger.getLogger(SendBroadcastExecutor.class.getName());

  private String commandSetType;

  private SocketChannel socketChannel;

  private StatisticService statisticService;


  private CountQueuesMode countQueuesMode;

  private SizeMessageMode sizeMessageMode;

  private Map<Long, CommandSetExecutor> handlesTimesExecutorsMap;

  public SendBroadcastExecutor(String commandSetType, SocketChannel socketChannel,
                               StatisticService statisticService,
                               CountQueuesMode countQueuesMode, SizeMessageMode sizeMessageMode) {
    this.commandSetType = commandSetType;
    this.socketChannel = socketChannel;
    this.statisticService = statisticService;
    this.countQueuesMode = countQueuesMode;
    this.sizeMessageMode = sizeMessageMode;
  }

  @Override
  public void execute(CommandSet commandSet) {
    sendCommand(createQueueListCommand());
  }

  @Override
  public void handleAnswer(Answer answer) {
    statisticService.write(commandSetType, answer);
    if(answer instanceof QueueListAnswer){
      QueueListAnswer ans = (QueueListAnswer) answer;
      List<Queue> queues = ans.getQueues();

    }
  }

  @Override
  public void setHandlesTimesExecutorsMap(Map<Long, CommandSetExecutor> handlesTimesExecutorsMap) {
    this.handlesTimesExecutorsMap = handlesTimesExecutorsMap;
  }

  private void sendCommand(Command command){
    try{
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      buffer.put(command.toString().getBytes());
      buffer.flip();
      socketChannel.write(buffer);
      buffer.clear();
    }catch (Exception e){
      logger.log(Level.SEVERE, "Error send command ", e);
    }
  }

  private QueueListCommand createQueueListCommand(){
    return new QueueListCommand();
  }

}
