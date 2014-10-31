package cmdset.executor;

import cmdset.CommandSet;
import cn.answer.Answer;
import cn.answer.QueueListAnswer;
import cn.command.Command;
import cn.command.QueueListCommand;
import cn.command.SendMessageCommand;
import cn.model.Queue;
import statistic.StatisticService;
import util.StringGenerator;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sends a broadcast messages
 */
public class SendMessageExecutor implements CommandSetExecutor{

  private static final Logger logger = Logger.getLogger(SendMessageExecutor.class.getName());

  public enum SizeMessageMode{MODE_SMALL,MODE_BIG}

  private String commandSetType;

  private SocketChannel socketChannel;

  private StatisticService statisticService;

  private SizeMessageMode sizeMessageMode;

  private Map<Long, CommandSetExecutor> handlesTimesExecutorsMap;

  private int clientId;

  public SendMessageExecutor(String commandSetType, SocketChannel socketChannel,
                             StatisticService statisticService,
                             SizeMessageMode sizeMessageMode,
                             int clientId) {
    this.commandSetType = commandSetType;
    this.socketChannel = socketChannel;
    this.statisticService = statisticService;
    this.sizeMessageMode = sizeMessageMode;
    this.clientId = clientId;
  }

  @Override
  public void execute(CommandSet commandSet) {
    QueueListCommand command = createQueueListCommand();
    handlesTimesExecutorsMap.put(command.getDateSend(), this);
    sendCommand(createQueueListCommand());
  }

  @Override
  public void handleAnswer(Answer answer) {
    statisticService.write(commandSetType, answer);
    if(answer instanceof QueueListAnswer){
      QueueListAnswer ans = (QueueListAnswer) answer;
      List<Queue> queues = ans.getQueues();
      if(queues.isEmpty()){
        return;
      }
      SendMessageCommand sendMessageCommand = createSendMessageCommand(queues);
      handlesTimesExecutorsMap.put(sendMessageCommand.getDateSend(), this);
      sendCommand(sendMessageCommand);


    }
  }

  @Override
  public void setHandlesTimesExecutorsMap(Map<Long, CommandSetExecutor> handlesTimesExecutorsMap) {
    this.handlesTimesExecutorsMap = handlesTimesExecutorsMap;
  }

  private void sendCommand(Command command){
    try{
      socketChannel.write(ByteBuffer.wrap(command.toString().getBytes()));
    }catch (Exception e){
      logger.log(Level.SEVERE, "Error send command ", e);
    }
  }

  private QueueListCommand createQueueListCommand(){
    QueueListCommand command = new QueueListCommand();
    command.setClientId(clientId);
    command.setDateSend(System.currentTimeMillis());
    return command;
  }

  private SendMessageCommand createSendMessageCommand(List<Queue> queues){
    String text = "";
    if(sizeMessageMode.equals(SizeMessageMode.MODE_BIG)){
      text = StringGenerator.generate(2000);
    } else if(sizeMessageMode.equals(SizeMessageMode.MODE_SMALL)){
      text = StringGenerator.generate(200);
    }

    SendMessageCommand command = new SendMessageCommand();
    command.setClientId(clientId);
    command.setText(text);

    double id;
    Queue queue;
    id = Math.random() * (queues.size()-1);
    queue = queues.get((int)id);
    command.setQueueId(queue.getId());
    command.setDateSend(System.currentTimeMillis());

    return command;
  }

}
