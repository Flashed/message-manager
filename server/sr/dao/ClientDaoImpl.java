package sr.dao;

import sr.context.AppContext;
import cn.model.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of ClientDao interface
 */
public class ClientDaoImpl implements ClientDao {

  private  static final Logger logger = Logger.getLogger(ClientDaoImpl.class.getName());

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

    ArrayList<Client> result = new ArrayList<>();
    String sql = "select * from clients";
    try(
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
    ){
      while(resultSet.next()){
        Client client = new Client();
        client.setId(resultSet.getInt("id"));
        result.add(client);
      }
    } catch (Exception e){
      RuntimeException wrap = new RuntimeException("Failed to get list of clients", e);
      logger.log(Level.SEVERE, "", e);
      throw wrap;
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void save(Client client) {
    String sql = "insert into clients values(?)";
    try(
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
            ){
      statement.setInt(1, client.getId());
      statement.execute();
      statement.close();
      connection.close();
      logger.fine("The " + client +" saved");
    } catch (Exception e){
      RuntimeException wrap = new RuntimeException("Failed to save client", e);
      logger.log(Level.SEVERE, "", e);
      throw wrap;
    }

  }

  private Connection getConnection() throws SQLException {
    return AppContext.getAppContext().getPoolingDataSource().getConnection();
  }
}
