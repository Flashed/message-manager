package cn.command;

/**
 *  A command - register client
 */
public class RegisterClientCommand extends  Command{

  public RegisterClientCommand() {
    setType(Command.REGISTER_CLIENT);
  }

  @Override
  public String toString() {
    return "<cmd>\n" +
            super.toString() +
            "</cmd>";
  }
}
