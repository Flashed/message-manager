package sr.dao;

import sr.context.AppContext;
import cn.model.Client;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of ClientDao interface
 */
public class ClientDaoImpl implements ClientDao {

  /**
   * {@inheritDoc}
   */
  @Override
  public Client get(int id) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Client> getAll() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void save(Client client) {

  }

  private Connection getConnection() throws SQLException {
    return AppContext.getAppContext().getPoolingDataSource().getConnection();
  }
}
