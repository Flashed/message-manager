import cmdset.CommandSet;
import cmdset.CommandSetStarter;
import cmdset.CommandSetStarterListener;
import cmdset.executor.CommandSetExecutor;
import cmdset.executor.CreateQueueExecutor;
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
public class Client implements CommandSetStarterListener {

  private static final Logger logger = Logger.getLogger(Client.class.getName());

  private String host;

  private int port;

  private long timeoutExec;

  private SocketChannel socketChannel;

  private Map<String, CommandSetExecutor> setExecutorsMap = new HashMap<>();

  private TaskRead taskRead;

  private StatisticService statisticService;


  public Client(String host, int port, long timeoutExec) {
    this.host = host;
    this.port = port;
    this.timeoutExec = timeoutExec;
  }

  public void connect(){
    try {
      socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
      taskRead = new TaskRead(socketChannel);
      new Thread(taskRead).start();
      logger.info(String.format("Connected to %s:%s",host, port));
      startExecutor();
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error of connection", e);
    }
  }


  private void startExecutor(){
    statisticService = new StatisticService();

    CreateQueueExecutor createQueueExecutor = new CreateQueueExecutor(socketChannel, statisticService);
    setExecutorsMap.put(CommandSet.TYPE_CREATE_QUEUES,createQueueExecutor);

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
      taskRead.setReadListener(executor);
      executor.execute(commandSet);
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
            Config.getExecTimeout());
    client.connect();
  }

}
