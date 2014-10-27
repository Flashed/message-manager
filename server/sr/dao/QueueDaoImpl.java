package sr.dao;

import sr.context.AppContext;
import sr.model.Queue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mikhail Zaitsev
 */
public class QueueDaoImpl implements QueueDao{

  private static final Logger logger = Logger.getLogger(QueueDaoImpl.class.getName());

  @Override
  public void save(Queue queue) {

    try {
      String sql = "insert into queues values (?)";
      Connection connection = getConnection();
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, queue.getId());
      if(statement.execute()){
        logger.info("Save " + queue);
      }
    } catch (SQLException e) {
      RuntimeException wrap = new RuntimeException("Error save queue", e);
      logger.log(Level.SEVERE, "", e);
      throw wrap;

    }

  }

  @Override
  public Queue get(int id) {
    return null;
  }

  @Override
  public void delete(int id) {

  }

  @Override
  public List<Queue> getAll() {
    List<Queue> result = new ArrayList<>();
    try {
      String sql = "select * from queues";
      Connection connection = getConnection();
      PreparedStatement statement = connection.prepareStatement(sql);
      ResultSet resultSet =  statement.executeQuery();
      if(resultSet != null){
        while(resultSet.next()){
          Queue queue = new Queue();
          queue.setId(resultSet.getInt("id"));
          result.add(queue);
        }
        return  result;
      }
    } catch (SQLException e) {
      RuntimeException wrap = new RuntimeException("Error get list of queues ", e);
      logger.log(Level.SEVERE, "", e);
      throw wrap;

    }
    return result;
  }

  private Connection getConnection() throws SQLException {
    return AppContext.getAppContext().getPoolingDataSource().getConnection();
  }

}
