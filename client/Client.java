import cmdset.CommandSet;
import cmdset.CommandSetStarter;
import cmdset.CommandSetStarterListener;
import cmdset.executor.*;
import cn.answer.Answer;
import cn.command.SendMessageCommand;
import read.ReadListener;
import read.TaskRead;
import statistic.StatisticService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Mikhail Zaitsev
 */
public class Client implements CommandSetStarterListener, ReadListener {

  private static final Logger logger = Logger.getLogger(Client.class.getName());

  private String host;

  private int port;

  private long timeoutExec;

  private SocketChannel socketChannel;

  private Map<String, CommandSetExecutor> setExecutorsMap = new HashMap<>();
  private Map<Long, CommandSetExecutor> handlesTimesExecutorsMap = new HashMap<>();

  private TaskRead taskRead;

  private StatisticService statisticService;

  private int clientId;


  public Client(String host, int port, long timeoutExec, int clientId) {
    this.host = host;
    this.port = port;
    this.timeoutExec = timeoutExec;
    this.clientId = clientId;
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
    statisticService.setClientId(clientId);

    CreateQueueExecutor createQueueExecutor = new CreateQueueExecutor(socketChannel, statisticService);
    createQueueExecutor.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_CREATE_QUEUE,createQueueExecutor);

    RegisterClientExecutor registerClientExecutor = new RegisterClientExecutor(socketChannel, statisticService, clientId);
    registerClientExecutor.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_REGISTER_CLIENT,registerClientExecutor);

    SendBroadcastExecutor sendBroadcastExecutor = new SendBroadcastExecutor(CommandSet.TYPE_SEND_SMALL_BROADCAST_TO_ONE,
            socketChannel, statisticService,
            SendBroadcastExecutor.CountQueuesMode.MODE_ONE,
            SendBroadcastExecutor.SizeMessageMode.MODE_SMALL,
            clientId);
    sendBroadcastExecutor.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_SEND_SMALL_BROADCAST_TO_ONE,sendBroadcastExecutor);

    SendBroadcastExecutor sendBroadcastExecutor2 = new SendBroadcastExecutor(CommandSet.TYPE_SEND_SMALL_BROADCAST_TO_SEVERAL,
            socketChannel, statisticService,
            SendBroadcastExecutor.CountQueuesMode.MODE_SEVERAL,
            SendBroadcastExecutor.SizeMessageMode.MODE_SMALL,
            clientId);
    sendBroadcastExecutor2.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_SEND_SMALL_BROADCAST_TO_SEVERAL, sendBroadcastExecutor2);

    SendBroadcastExecutor sendBroadcastExecutor3 = new SendBroadcastExecutor(CommandSet.TYPE_SEND_BIG_BROADCAST_TO_ONE,
            socketChannel, statisticService,
            SendBroadcastExecutor.CountQueuesMode.MODE_ONE,
            SendBroadcastExecutor.SizeMessageMode.MODE_BIG,
            clientId);
    sendBroadcastExecutor3.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_SEND_BIG_BROADCAST_TO_ONE, sendBroadcastExecutor3);

    SendBroadcastExecutor sendBroadcastExecutor4 = new SendBroadcastExecutor(CommandSet.TYPE_SEND_BIG_BROADCAST_TO_SEVERAL,
            socketChannel, statisticService,
            SendBroadcastExecutor.CountQueuesMode.MODE_SEVERAL,
            SendBroadcastExecutor.SizeMessageMode.MODE_BIG,
            clientId);
    sendBroadcastExecutor4.setHandlesTimesExecutorsMap(handlesTimesExecutorsMap);
    setExecutorsMap.put(CommandSet.TYPE_SEND_BIG_BROADCAST_TO_SEVERAL,sendBroadcastExecutor4);

    CommandSetStarter commandGenerator = new CommandSetStarter(timeoutExec, this);
    commandGenerator.start();
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
    if(handlesTimesExecutorsMap.containsKey(answer.getDateSend())){
      handlesTimesExecutorsMap.remove(answer.getDateSend()).handleAnswer(answer);
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
            Config.getClientId());
    client.connect();
  }
}
