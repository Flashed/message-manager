
public class CmdExec {

  private String commandType;

  private int count;

  public CmdExec() {
    setCount(1);
  }

  public String getCommandType() {
    return commandType;
  }

  public void setCommandType(String commandType) {
    this.commandType = commandType;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
