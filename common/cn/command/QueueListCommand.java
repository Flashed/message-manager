package cn.command;

/**
 * A command - get list of queue
 */
public class QueueListCommand extends Command{

  public QueueListCommand() {
    setType(QUEUE_LIST);
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
