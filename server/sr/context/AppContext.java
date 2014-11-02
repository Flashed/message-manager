package sr.context;

import sr.Config;
import sr.Server;
import sr.dao.*;
import org.postgresql.jdbc2.optional.PoolingDataSource;

import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Application sr.context
 */
public class AppContext {

  private static AppContext appContext;

  private  PoolingDataSource poolingDataSource;

  private ThreadPoolExecutor poolExecutor;

  private Server server;

  private Config config;

  private ClientDao clientDao;

  private QueueDao queueDao;

  private MessageDao messageDao;

  private Map<Channel, StringBuilder> commandBuffers = new HashMap();

  private AppContext() {

  }

  public void init(){
    config = new Config();
    config.init();

    poolingDataSource = new PoolingDataSource();
    poolingDataSource.setUser(Config.getDBUsername());
    poolingDataSource.setPassword(Config.getDBPassword());
    poolingDataSource.setServerName(Config.getDBHost());
    poolingDataSource.setDatabaseName(Config.getDBName());
    poolingDataSource.setMaxConnections(Config.getDBConnectionsNumber());

    clientDao = new ClientDaoImpl();
    queueDao = new QueueDaoImpl();
    messageDao = new MessageDaoImpl();

    poolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Config.getNumberThread());

    server = new Server(Config.getPort());
    server.setPoolExecutor(poolExecutor);

  }

  public static AppContext getAppContext() {
    if(appContext == null){
      appContext = new AppContext();
      appContext.init();
    }
    return appContext;
  }

  public PoolingDataSource getPoolingDataSource() {
    return poolingDataSource;
  }

  public Server getServer() {
    return server;
  }

  public Map<Channel, StringBuilder> getCommandBuffers() {
    return commandBuffers;
  }

  public Config getConfig() {
    return config;
  }

  public ThreadPoolExecutor getPoolExecutor() {
    return poolExecutor;
  }

  public ClientDao getClientDao() {
    return clientDao;
  }

  public QueueDao getQueueDao() {
    return queueDao;
  }

  public MessageDao getMessageDao() {
    return messageDao;
  }

}
