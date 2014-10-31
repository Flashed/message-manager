package sr.dao;

import cn.model.Message;

/**
 * Message DAO
 */
public interface MessageDao {

  void save(Message message);

  void delete(Message message);
}
