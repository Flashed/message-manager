package cmdset.executor;

import cmdset.CommandSet;
import cn.answer.Answer;
import cn.command.CreateQueueCommand;
import statistic.StatisticService;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Create queue executor
 */
public class CreateQueueExecutor implements CommandSetExecutor{

  private static final Logger logger = Logger.getLogger(CreateQueueExecutor.class.getName());

  private SocketChannel socketChannel;

  private StatisticService statisticService;

  public CreateQueueExecutor(SocketChannel socketChannel, StatisticService statisticService) {
    this.socketChannel = socketChannel;
    this.statisticService = statisticService;
  }

  @Override
  public void execute(CommandSet commandSet) {
    try{
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      buffer.put(createCreateQueueCommand().toString().getBytes());
      buffer.flip();
      socketChannel.write(buffer);
      buffer.clear();
    }catch (Exception e){
      logger.log(Level.SEVERE, "Error send command ", e);
    }
  }

  @Override
  public void onReadAnswer(Answer answer) {
    statisticService.write(answer);
  }

  private CreateQueueCommand createCreateQueueCommand(){
    CreateQueueCommand cmd = new CreateQueueCommand();
    cmd.setQueueId((int)(Math.random()*100000));
    cmd.setDateSend(System.currentTimeMillis());
    return cmd;
  }
}
