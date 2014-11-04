package sr.dao;

import cn.model.Message;

import java.sql.Connection;

/**
 * Message DAO
 */
public interface MessageDao {

  void save(Message message);

  void delete(Message message);

  Message last(int queueId, int receiverId);

  Message last(int queueId, int receiverId, int senderId);
}
