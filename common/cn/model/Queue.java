package cn.model;

/**
 * Queue
 */
public class Queue {

  private int id;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Queue{" +
            "id=" + id +
            '}';
  }
}
