package cn.command;

/**
 * Get my message
 */
public class GetMeMessageCommand extends Command{

  private int queueId;

  public GetMeMessageCommand() {

    setType(GET_ME_MESSAGE);

  }

  public int getQueueId() {
    return queueId;
  }

  public void setQueueId(int queueId) {
    this.queueId = queueId;
  }

  @Override
  public String toString(){
    return "<cmd>\n" +
            "    <type>"+ getType() +"</type>\n" +
            "    <clientId>"+ getClientId() +"</clientId>\n" +
            "    <queueId>"+ getQueueId() +"</queueId>\n" +
            "    <dateSend>"+ getDateSend() +"</dateSend>\n" +
            "    <commandSetId>"+ getCommandSetId() +"</commandSetId>\n" +
            "</cmd>";
  }
}
