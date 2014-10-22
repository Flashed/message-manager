package command;

/**
 * @author Mikhail Zaitsev
 */
public abstract class Command {

  private String type;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
