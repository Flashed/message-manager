package cn.model;

/**
 * Client
 */
public class Client {

  private int id;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Client{" +
            "id=" + id +
            '}';
  }
}
