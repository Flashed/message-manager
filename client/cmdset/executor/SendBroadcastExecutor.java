package cmdset.executor;

import cmdset.CommandSet;
import cmdset.executor.params.CountQueuesMode;
import cmdset.executor.params.SizeMessageMode;
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
public class SendBroadcastExecutor implements CommandSetExecutor{

  private static final Logger logger = Logger.getLogger(SendBroadcastExecutor.class.getName());


  private String commandSetType;

  private SocketChannel socketChannel;

  private StatisticService statisticService;


  private CountQueuesMode countQueuesMode;

  private SizeMessageMode sizeMessageMode;

  private Map<Long, CommandSetExecutor> handlesTimesExecutorsMap;

  private CommandSetExecutorListener listener;

  private int clientId;

  public SendBroadcastExecutor(String commandSetType, SocketChannel socketChannel,
                               StatisticService statisticService,
                               CountQueuesMode countQueuesMode, SizeMessageMode sizeMessageMode,
                               int clientId) {
    this.commandSetType = commandSetType;
    this.socketChannel = socketChannel;
    this.statisticService = statisticService;
    this.countQueuesMode = countQueuesMode;
    this.sizeMessageMode = sizeMessageMode;
    this.clientId = clientId;
  }

  @Override
  public void execute(CommandSet commandSet) {
    QueueListCommand command = createQueueListCommand(commandSet);
    synchronized (handlesTimesExecutorsMap){
      if(handlesTimesExecutorsMap.containsKey(command.getCommandId())){
        logger.log(Level.SEVERE, "handlesTimesExecutorsMap already contains key");
      }
      handlesTimesExecutorsMap.put(command.getCommandId(), this);
    }
    sendCommand(command);
  }

  @Override
  public void handleAnswer(Answer answer) {
    statisticService.write(commandSetType, answer);
    if(answer instanceof QueueListAnswer){
      QueueListAnswer ans = (QueueListAnswer) answer;
      List<Queue> queues = ans.getQueues();
      if(queues.isEmpty()){
        logger.warning("list of Queues is empty");
        return;
      }
      if(countQueuesMode.equals(CountQueuesMode.MODE_ONE)){
        SendMessageCommand sendMessageCommand = createSendMessageCommand(queues, answer);
        synchronized (handlesTimesExecutorsMap){
          if(handlesTimesExecutorsMap.containsKey(sendMessageCommand.getCommandId())){
            logger.log(Level.SEVERE, "handlesTimesExecutorsMap already contains key");
          }
          handlesTimesExecutorsMap.put(sendMessageCommand.getCommandId(), this);
        }
        sendCommand(sendMessageCommand);
      } else {
        int c = 10;
        if(queues.size()<c) {
          c = queues.size();
        }
        for(int i = 0; i < c; i++){
          SendMessageCommand sendMessageCommand = createSendMessageCommand(queues, answer);
          synchronized (handlesTimesExecutorsMap){
            if(handlesTimesExecutorsMap.containsKey(sendMessageCommand.getCommandId())){
              logger.log(Level.SEVERE, "handlesTimesExecutorsMap already contains key");
            }
            handlesTimesExecutorsMap.put(sendMessageCommand.getCommandId(), this);
          }
          sendCommand(sendMessageCommand);
        }
      }

    } else {
      if(logger.isLoggable(Level.FINE)){
        logger.fine(getClass().getName() + "finished.");
      }
      listener.onFinished();
    }
  }

  @Override
  public void setHandlesTimesExecutorsMap(Map<Long, CommandSetExecutor> handlesTimesExecutorsMap) {
    this.handlesTimesExecutorsMap = handlesTimesExecutorsMap;
  }

  @Override
  public void setListener(CommandSetExecutorListener listener) {
    this.listener = listener;
  }

  private void sendCommand(Command command){
    try{
      socketChannel.write(ByteBuffer.wrap(command.toString().getBytes()));
      logger.info("Send command " + command);
    }catch (Exception e){
      logger.log(Level.SEVERE, "Error send command ", e);
    }
  }

  private QueueListCommand createQueueListCommand(CommandSet commandSet){
    QueueListCommand command = new QueueListCommand();
    command.setCommandSetId(commandSet.getId());
    command.setClientId(clientId);
    command.setCommandId(System.nanoTime());
    command.setDateSend(System.currentTimeMillis());
    return command;
  }

  private SendMessageCommand createSendMessageCommand(List<Queue> queues, Answer answer){
    String text = "";
    if(sizeMessageMode.equals(SizeMessageMode.MODE_BIG)){
      text = StringGenerator.generate(2000);
    } else if(sizeMessageMode.equals(SizeMessageMode.MODE_SMALL)){
      text = StringGenerator.generate(200);
    }

    SendMessageCommand command = new SendMessageCommand();
    command.setCommandSetId(answer.getCommandSetId());
    command.setClientId(clientId);
    command.setText(text);

    double id;
    Queue queue;
    id = Math.random() * (queues.size()-1);
    queue = queues.get((int)id);
    command.setQueueId(queue.getId());
    command.setCommandId(System.nanoTime());
    command.setDateSend(System.currentTimeMillis());

    return command;
  }

}
