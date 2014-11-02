package sr.dao;

import cn.model.Message;
import sr.context.AppContext;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class MessageDaoImpl implements MessageDao{

  private static final Logger logger = Logger.getLogger(MessageDaoImpl.class.getName());

  @Override
  public void save(Message message) {

    String sql = "insert into messages values (default, ?, ?, ?, ?)";
    try (
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
    ) {

      statement.setInt(1, message.getQueueId());
      statement.setInt(2, message.getSenderId());
      statement.setInt(3, message.getReceiverId());
      statement.setString(4, message.getText());
      statement.execute();
      ResultSet resultSet = statement.getGeneratedKeys();
      if(resultSet.next()){
        message.setId(resultSet.getInt(1));
        logger.info("Save " + message);
      }
      resultSet.close();
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
    String sql = "delete from messages where id=?";
    try(
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ){
      statement.setInt(1, message.getId());
      if(statement.execute()){
        logger.info("Message " + message+ " deleted");
      }
      connection.close();
      statement.close();
    } catch (Exception e){
      RuntimeException wrap = new RuntimeException("Error delete message", e);
      logger.log(Level.SEVERE, "", e);
      throw wrap;
    }

  }

  @Override
  public Message get(int queueId, int receiverId) {

    String sql = "select * from messages where queueId=? and receiverId=? limit 1";

    try (
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
    ) {

      statement.setInt(1, queueId);
      statement.setInt(2, receiverId);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()){
        Message message = new Message();
        message.setText(resultSet.getString("mess_text"));
        message.setId(resultSet.getInt("id"));
        message.setQueueId(resultSet.getInt("queueid"));
        message.setSenderId(resultSet.getInt("senderid"));
        message.setReceiverId(resultSet.getInt("receiverid"));

        resultSet.close();
        statement.close();
        connection.close();
        return message;
      }
      resultSet.close();
      statement.close();
      connection.close();
    } catch (Exception e) {
      RuntimeException wrap = new RuntimeException("Error get message", e);
      logger.log(Level.SEVERE, "", e);
      throw wrap;

    }


    return null;
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

}
