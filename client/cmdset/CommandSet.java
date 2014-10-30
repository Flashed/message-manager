package cmdset;

/**
 * A set of commands
 */
public class CommandSet {

  public static final String TYPE_CREATE_QUEUE = "create_queue";
  public static final String TYPE_REGISTER_CLIENT = "register_client";
  public static final String TYPE_SEND_SMALL_BROADCAST_TO_ONE = "send_small_broadcast_to_one";

  private String type;

  private int execCount;

  public CommandSet() {
    setExecCount(1);
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getExecCount() {
    return execCount;
  }

  public void setExecCount(int execCount) {
    this.execCount = execCount;
  }
}
