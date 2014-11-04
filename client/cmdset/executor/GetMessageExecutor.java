package cmdset.executor;

import cmdset.CommandSet;
import cn.answer.Answer;
import cn.answer.MessageAnswer;
import cn.answer.QueueListAnswer;
import cn.command.Command;
import cn.command.GetMeMessageCommand;
import cn.command.QueueListCommand;
import cn.model.Queue;
import statistic.StatisticService;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class GetMessageExecutor implements CommandSetExecutor {

  private static final Logger logger = Logger.getLogger(GetMessageExecutor.class.getName());

  private Map<Long, CommandSetExecutor> handlesTimesExecutorsMap;

  private String commandSetType = CommandSet.TYPE_GET_BROADCAST_MESSAGE;

  private SocketChannel socketChannel;

  private StatisticService statisticService;

  private int clientId;

  public GetMessageExecutor(SocketChannel socketChannel, StatisticService statisticService, int clientId) {
    this.socketChannel = socketChannel;
    this.statisticService = statisticService;
    this.clientId = clientId;
  }

  @Override
  public void execute(CommandSet commandSet) {
    QueueListCommand command = createQueueListCommand(commandSet);
    synchronized (handlesTimesExecutorsMap){
      if(handlesTimesExecutorsMap.containsKey(command.getDateSend())){
        logger.log(Level.SEVERE, "handlesTimesExecutorsMap already contains key");
      }
      handlesTimesExecutorsMap.put(command.getDateSend(), this);
      sendCommand(command);
    }
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
      GetMeMessageCommand command = createGetMeMessageCommand(queues, answer);
      synchronized (handlesTimesExecutorsMap){
        if(handlesTimesExecutorsMap.containsKey(command.getDateSend())){
          logger.log(Level.SEVERE, "handlesTimesExecutorsMap already contains key");
        }
        handlesTimesExecutorsMap.put(command.getDateSend(), this);
      }
      sendCommand(command);
    } else if (answer instanceof MessageAnswer){
      try {
        FileWriter writer = new FileWriter("./messages/" + ((MessageAnswer) answer).getMessageId() +".txt");
        writer.write(((MessageAnswer) answer).getText());
        writer.flush();
        writer.close();
        logger.info("Save message to file "  + "./messages/"+ ((MessageAnswer) answer).getMessageId() +".txt");
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Error save message to folder", e);
      }
    }
  }

  @Override
  public void setHandlesTimesExecutorsMap(Map<Long, CommandSetExecutor> handlesTimesExecutorsMap) {
    this.handlesTimesExecutorsMap = handlesTimesExecutorsMap;
  }

  private QueueListCommand createQueueListCommand(CommandSet commandSet){
    QueueListCommand command = new QueueListCommand();
    command.setCommandSetId(commandSet.getId());
    command.setClientId(clientId);
    command.setDateSend(System.nanoTime());
    return command;
  }

  private GetMeMessageCommand createGetMeMessageCommand(List<Queue> queues, Answer answer){
    GetMeMessageCommand command = new GetMeMessageCommand();
    command.setCommandSetId(answer.getCommandSetId());
    command.setClientId(clientId);

    double id;
    Queue queue;
    id = Math.random() * (queues.size()-1);
    queue = queues.get((int)id);
    command.setQueueId(queue.getId());
    command.setDateSend(System.nanoTime());

    return command;
  }

  private void sendCommand(Command command){
    try{
      socketChannel.write(ByteBuffer.wrap(command.toString().getBytes()));
      logger.info("Send command " + command);
    }catch (Exception e){
      logger.log(Level.SEVERE, "Error send command ", e);
    }
  }
}
