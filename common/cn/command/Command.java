package cn.command;

/**
 * The abstract sr.cn.command
 */
public abstract class Command {

  public static final String CREATE_QUEUE = "create_queue";
  public static final String QUEUE_LIST = "queue_list";


  private String type;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
