package sr.dao;

import cn.model.Message;
import sr.context.AppContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class MessageDaoImpl implements MessageDao{

  private static final Logger logger = Logger.getLogger(MessageDaoImpl.class.getName());

  @Override
  public void save(Message message) {

    Integer messageCount = getMessageCount();
    synchronized (messageCount){
      if(messageCount == -1){
        AppContext.getAppContext().setMessagesCount(getLastMessageCount());
      }
    }

    String sql = "insert into messages values (?, ?, ?, ?, ?)";
    try (
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
    ) {

      messageCount = getMessageCount();
      synchronized (messageCount){
        AppContext.getAppContext().setMessagesCount(messageCount + 1);
        message.setId(getMessageCount());
      }
      statement.setInt(1, message.getId());
      statement.setInt(2, message.getQueueId());
      statement.setInt(3, message.getSenderId());
      statement.setInt(4, message.getReceiverId());
      statement.setString(5, message.getText());
      if(statement.execute()){
        logger.info("Save " + message);
      }
      statement.close();
      connection.close();
    } catch (SQLException e) {
      RuntimeException wrap = new RuntimeException("Error save message", e);
      logger.log(Level.SEVERE, "", e);
      throw wrap;

    }

  }

  @Override
  public void delete(Message message) {

  }

  private Integer getLastMessageCount(){
    String sql = "select id from queues order by id desc limit 1";
    Integer result = 0;
    try (
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      ResultSet resultSet = statement.executeQuery();
      if(resultSet.next()) {
        result = resultSet.getInt("id");
      }
      resultSet.close();
      statement.close();
      connection.close();
    } catch (SQLException e) {
      RuntimeException wrap = new RuntimeException("Error save queue", e);
      logger.log(Level.SEVERE, "", e);
      throw wrap;

    }
    return result;
  }

  private Connection getConnection() throws SQLException {
    return AppContext.getAppContext().getPoolingDataSource().getConnection();
  }

  private Integer getMessageCount(){
    return AppContext.getAppContext().getMessagesCount();
  }
}
