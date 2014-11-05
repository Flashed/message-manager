package cmdset.executor;

import cmdset.CommandSet;
import cmdset.executor.params.CountQueuesMode;
import cmdset.executor.params.SizeMessageMode;
import cn.answer.Answer;
import cn.answer.ClientListAnswer;
import cn.answer.QueueListAnswer;
import cn.command.ClientListCommand;
import cn.command.Command;
import cn.command.QueueListCommand;
import cn.command.SendMessageCommand;
import cn.model.Client;
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
 * Sends a messages to other clients
 */
public class SendMessageExecutor implements CommandSetExecutor{

  private static final Logger logger = Logger.getLogger(SendMessageExecutor.class.getName());

  private String commandSetType;

  private SocketChannel socketChannel;

  private StatisticService statisticService;

  private CountQueuesMode countQueuesMode;

  private SizeMessageMode sizeMessageMode;

  private Map<Long, CommandSetExecutor> handlesTimesExecutorsMap;

  private int clientId;

  List<Client> clients;

  public SendMessageExecutor(String commandSetType, SocketChannel socketChannel,
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
    ClientListCommand command = createClientListCommand(commandSet);
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

    } else if(answer instanceof ClientListAnswer){
      clients = ((ClientListAnswer) answer).getClients();
      QueueListCommand command = createQueueListCommand(answer);
      synchronized (handlesTimesExecutorsMap){
        if(handlesTimesExecutorsMap.containsKey(command.getCommandId())){
          logger.log(Level.SEVERE, "handlesTimesExecutorsMap already contains key");
        }
        handlesTimesExecutorsMap.put(command.getCommandId(), this);
      }
      sendCommand(command);
    }
  }

  @Override
  public void setHandlesTimesExecutorsMap(Map<Long, CommandSetExecutor> handlesTimesExecutorsMap) {
    this.handlesTimesExecutorsMap = handlesTimesExecutorsMap;
  }

  private void sendCommand(Command command){
    try{
      socketChannel.write(ByteBuffer.wrap(command.toString().getBytes()));
      logger.info("Send command " + command);
    }catch (Exception e){
      logger.log(Level.SEVERE, "Error send command ", e);
    }
  }

  private QueueListCommand createQueueListCommand(Answer answer){
    QueueListCommand command = new QueueListCommand();
    command.setCommandSetId(answer.getCommandSetId());
    command.setClientId(clientId);
    command.setCommandId(System.nanoTime());
    command.setDateSend(System.currentTimeMillis());
    return command;
  }

  private ClientListCommand createClientListCommand(CommandSet commandSet){
    ClientListCommand command = new ClientListCommand();
    command.setCommandSetId(commandSet.getId());
    command.setCommandId(System.nanoTime());
    command.setDateSend(System.currentTimeMillis());
    return  command;
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

    Client client;
    synchronized (clients){
      id = Math.random() * (clients.size()-1);
      client = clients.get((int)id);
      command.setRecipientId(client.getId());

    }
    command.setCommandId(System.nanoTime());
    command.setDateSend(System.currentTimeMillis());
    return command;
  }

}
