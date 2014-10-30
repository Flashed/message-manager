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
            "    <type>"+ getType() +"</type>\n" +
            "    <clientId>"+ getClientId() +"</clientId>\n" +
            "    <dateSend>"+ getDateSend() +"</dateSend>\n" +
            "</cmd>";
  }
}
