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
            "    <dateSend>"+ getDateSend() +"</dateSend>\n" +
            "    <commandSetId>"+ getCommandSetId() +"</commandSetId>\n" +
            "</cmd>";
  }
}
