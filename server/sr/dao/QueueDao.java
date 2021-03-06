package sr.dao;

import cn.model.Queue;

import java.util.List;

/**
 * Dao of Queue
 */
public interface QueueDao {


  /**
   * Save queue
   *
   * @param queue a queue
   */
  void save(Queue queue);

  /**
   * Get Queue by id
   *
   * @param id id
   * @return a Queue
   */
  Queue get(int id);

  /**
   * Delete a Queue by id
   *
   * @param id id of Queue
   */
  void delete(int id);

  /**
   * Get all queues
   * @return  all queues
   */
  List<Queue> getAll();

}
