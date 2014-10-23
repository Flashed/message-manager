package server.command;

/**
 * The abstract server.command
 */
public abstract class Command {

  public static final String CREATE_QUEUE = "create_queue";


  private String type;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
