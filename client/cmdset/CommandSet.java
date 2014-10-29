package cmdset;

/**
 * A set of commands
 */
public class CommandSet {

  public static final String TYPE_CREATE_QUEUES = "create_queue";

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
