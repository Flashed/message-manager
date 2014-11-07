import cmdset.CommandSet;
import cmdset.CommandSetStarter;
import cmdset.CommandSetStarterListener;
import cmdset.executor.*;
import cmdset.executor.params.CountQueuesMode;
import cmdset.executor.params.SizeMessageMode;
import cn.answer.Answer;
import read.ReadListener;
import read.TaskRead;
import statistic.StatisticService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 */
public class Client implements CommandSetStarterListener, ReadListener {

  private static final Logger logger = Logger.getLogger(Client.class.getName());

  private String host;

  private int port;

  private long timeoutExec;

  private SocketChannel socketChannel;

  private Map<String, CommandSetExecutor> setExecutorsMap = new HashMap<>();
  private final Map<Long, CommandSetExecutor> handlesTimesExecutorsMap = new HashMap<>();

  private TaskRead taskRead;

  private StatisticService statisticService;

  private int clientId;

  private String statisticFolder;

  public Client(String host, int port, long timeoutExec, int clientId, String statisticFolder) {
    this.host = host;
    this.port = port;
    this.timeoutExec = timeoutExec;
    this.clientId = clientId;
    this.statisticFolder = statisticFolder;
  }

  public void connect(){
    try {
      socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
      taskRead = new TaskRead(socketChannel);
      taskRead.setReadListener(this);
      new Thread(taskRead).start();
      logger.info(String.format("Connected to %s:%s",host, port));
      startExecutor();
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error of connection", e);
    }
  }


  private void startExecutor(){
    statisticService = new StatisticService();
    statisticService.setFolder(statisticFolder);
    statisticService.setClientId(clientId);
    statisticService.init();

    CreateQueueExecutor createQueueExecutor = new CreateQueueExecutor(socketChannel, statisticService);
    createQueueExecutor.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_CREATE_QUEUE,createQueueExecutor);

    RegisterClientExecutor registerClientExecutor = new RegisterClientExecutor(socketChannel, statisticService, clientId);
    registerClientExecutor.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_REGISTER_CLIENT,registerClientExecutor);

    SendBroadcastExecutor sendBroadcastExecutor = new SendBroadcastExecutor(CommandSet.TYPE_SEND_SMALL_BROADCAST_TO_ONE,
            socketChannel, statisticService,
            CountQueuesMode.MODE_ONE,
            SizeMessageMode.MODE_SMALL,
            clientId);
    sendBroadcastExecutor.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_SEND_SMALL_BROADCAST_TO_ONE,sendBroadcastExecutor);

    SendBroadcastExecutor sendBroadcastExecutor2 = new SendBroadcastExecutor(CommandSet.TYPE_SEND_SMALL_BROADCAST_TO_SEVERAL,
            socketChannel, statisticService,
            CountQueuesMode.MODE_SEVERAL,
            SizeMessageMode.MODE_SMALL,
            clientId);
    sendBroadcastExecutor2.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_SEND_SMALL_BROADCAST_TO_SEVERAL, sendBroadcastExecutor2);

    SendBroadcastExecutor sendBroadcastExecutor3 = new SendBroadcastExecutor(CommandSet.TYPE_SEND_BIG_BROADCAST_TO_ONE,
            socketChannel, statisticService,
            CountQueuesMode.MODE_ONE,
            SizeMessageMode.MODE_BIG,
            clientId);
    sendBroadcastExecutor3.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_SEND_BIG_BROADCAST_TO_ONE, sendBroadcastExecutor3);

    SendBroadcastExecutor sendBroadcastExecutor4 = new SendBroadcastExecutor(CommandSet.TYPE_SEND_BIG_BROADCAST_TO_SEVERAL,
            socketChannel, statisticService,
            CountQueuesMode.MODE_SEVERAL,
            SizeMessageMode.MODE_BIG,
            clientId);
    sendBroadcastExecutor4.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_SEND_BIG_BROADCAST_TO_SEVERAL,sendBroadcastExecutor4);




    SendMessageExecutor sendMessageExecutor = new SendMessageExecutor(CommandSet.TYPE_SEND_SMALL_MESSAGE_TO_ONE,
            socketChannel, statisticService,
            CountQueuesMode.MODE_ONE,
            SizeMessageMode.MODE_SMALL,
            clientId);
    sendMessageExecutor.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_SEND_SMALL_MESSAGE_TO_ONE,sendMessageExecutor);

    SendMessageExecutor sendMessageExecutor2 = new SendMessageExecutor(CommandSet.TYPE_SEND_SMALL_MESSAGE_TO_SEVERAL,
            socketChannel, statisticService,
            CountQueuesMode.MODE_SEVERAL,
            SizeMessageMode.MODE_SMALL,
            clientId);
    sendMessageExecutor2.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_SEND_SMALL_MESSAGE_TO_SEVERAL, sendMessageExecutor2);

    SendMessageExecutor sendMessageExecutor3 = new SendMessageExecutor(CommandSet.TYPE_SEND_BIG_MESSAGE_TO_ONE,
            socketChannel, statisticService,
            CountQueuesMode.MODE_ONE,
            SizeMessageMode.MODE_BIG,
            clientId);
    sendMessageExecutor3.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_SEND_BIG_MESSAGE_TO_ONE, sendMessageExecutor3);

    SendMessageExecutor sendMessageExecutor4 = new SendMessageExecutor(CommandSet.TYPE_SEND_BIG_MESSAGE_TO_SEVERAL,
            socketChannel, statisticService,
            CountQueuesMode.MODE_SEVERAL,
            SizeMessageMode.MODE_BIG,
            clientId);
    sendMessageExecutor4.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_SEND_BIG_MESSAGE_TO_SEVERAL,sendMessageExecutor4);


    GetBroadcastMessageExecutor getBroadcastMessageExecutor1 = new GetBroadcastMessageExecutor(socketChannel, statisticService,
            clientId, CommandSet.TYPE_GET_BROADCAST_MESSAGE);
    getBroadcastMessageExecutor1.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_GET_BROADCAST_MESSAGE, getBroadcastMessageExecutor1);

    GetBroadcastMessageExecutor getBroadcastMessageExecutor2 = new GetBroadcastMessageExecutor(socketChannel, statisticService,
            clientId, CommandSet.TYPE_GET_AND_DELETE_BROADCAST_MESSAGE);
    getBroadcastMessageExecutor2.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_GET_AND_DELETE_BROADCAST_MESSAGE, getBroadcastMessageExecutor2);

    GetMessageExecutor getMessageExecutor1 = new GetMessageExecutor(socketChannel, statisticService, clientId,
            CommandSet.TYPE_GET_MESSAGE);
    getMessageExecutor1.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_GET_MESSAGE, getMessageExecutor1);

    GetMessageExecutor getMessageExecutor2 = new GetMessageExecutor(socketChannel, statisticService, clientId,
            CommandSet.TYPE_GET_AND_DELETE_MESSAGE);
    getMessageExecutor2.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_GET_AND_DELETE_MESSAGE, getMessageExecutor2);

    GetMessageExecutor getMessageExecutor3 = new GetMessageExecutor(socketChannel, statisticService, clientId,
            CommandSet.TYPE_GET_MESSAGE_FROM_SENDER);
    getMessageExecutor3.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_GET_MESSAGE_FROM_SENDER, getMessageExecutor3);

    GetMessageExecutor getMessageExecutor4 = new GetMessageExecutor(socketChannel, statisticService, clientId,
            CommandSet.TYPE_GET_AND_DELETE_MESSAGE_FROM_SENDER);
    getMessageExecutor4.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_GET_AND_DELETE_MESSAGE_FROM_SENDER, getMessageExecutor4);


    CommandSetStarter starter = new CommandSetStarter(timeoutExec, this);
    Collection<CommandSetExecutor> executors =  setExecutorsMap.values();
    for(CommandSetExecutor executor: executors){
      executor.setListener(starter);
    }
    starter.start();
  }


  @Override
  public void onGetCommandSet(CommandSet commandSet) {
    if(commandSet == null){
      return;
    }
    if(setExecutorsMap.containsKey(commandSet.getType())){
      CommandSetExecutor executor = setExecutorsMap.get(commandSet.getType());
      executor.execute(commandSet);
    }

  }

  @Override
  public void onReadAnswer(Answer answer) {
    synchronized (handlesTimesExecutorsMap){
      Collection<Long> keys = handlesTimesExecutorsMap.keySet();
      logger.fine("keys " + keys + "\n answer key " + answer.getCommandId());
      if(handlesTimesExecutorsMap.containsKey(answer.getCommandId())){
        handlesTimesExecutorsMap.remove(answer.getCommandId()).handleAnswer(answer);
      }
    }
  }

  public static void main(String... args){

    try {
      LogManager.getLogManager().readConfiguration(Client.class.getClassLoader().getResourceAsStream("logging.properties"));
    } catch (SecurityException | IOException e1) {
      logger.log(Level.SEVERE, "Error config logger", e1);
    }

    new Config().init();

    Client client = new Client(Config.getServerHost(),
            Config.getServerPort(),
            Config.getExecTimeout(),
            Config.getClientId(),
            Config.getStatisticFolder());
    client.connect();
  }
}
