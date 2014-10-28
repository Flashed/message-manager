package sr.dao;

import sr.context.AppContext;
import cn.model.Queue;

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

    String sql = "insert into queues values (?)";
    try (
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, queue.getId());
      if(statement.execute()){
        logger.info("Save " + queue);
      }
      statement.close();
      connection.close();
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
    String sql = "select * from queues";
    try(
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet =  statement.executeQuery()
            ) {

      if(resultSet != null){
        while(resultSet.next()){
          Queue queue = new Queue();
          queue.setId(resultSet.getInt("id"));
          result.add(queue);
        }
        resultSet.close();
        statement.close();
        connection.close();
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
