package cn.command;

/**
 */
public class ClientListCommand extends Command{
  public ClientListCommand() {
    setType(CLIENT_LIST);
  }

  @Override
  public String toString() {
    return "<cmd>\n" +
            "    <type>" + getType() + "</type>\n" +
            "    <clientId>"+ getClientId() +"</clientId>\n" +
            "    <commandId>"+ getCommandId() +"</commandId>\n" +
            "    <commandSetId>"+ getCommandSetId() +"</commandSetId>\n" +
            "    <dateSend>"+getDateSend()+"</dateSend>\n" +
            "</cmd>";
  }

}
