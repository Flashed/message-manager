package server.context;

import server.Config;
import server.Server;
import server.dao.ClientDao;
import org.postgresql.jdbc2.optional.PoolingDataSource;
import server.dao.ClientDaoImpl;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Application server.context
 */
public class AppContext {

  private static AppContext appContext;

  private  PoolingDataSource poolingDataSource;

  private ThreadPoolExecutor poolExecutor;

  private Server server;

  private Config config;

  private ClientDao clientDao;

  public AppContext() {

    config = new Config();
    config.init();

    poolingDataSource = new PoolingDataSource();
    poolingDataSource.setUser(Config.getDBUsername());
    poolingDataSource.setPassword(Config.getDBPassword());
    poolingDataSource.setServerName(Config.getDBHost());
    poolingDataSource.setDatabaseName(Config.getDBName());
    poolingDataSource.setMaxConnections(Config.getDBConnectionsNumber());

    clientDao = new ClientDaoImpl();

    poolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Config.getNumberThread());

    server = new Server(Config.getPort());
    server.setPoolExecutor(poolExecutor);

  }

  public static AppContext getAppContext() {
    if(appContext == null){
      appContext = new AppContext();
    }
    return appContext;
  }

  public PoolingDataSource getPoolingDataSource() {
    return poolingDataSource;
  }

  public Server getServer() {
    return server;
  }

  public Config getConfig() {
    return config;
  }
}
