package cn.command;

/**
 * Get my message
 */
public class GetMeMessageCommand extends Command{

  private int queueId;

  private boolean delete;

  private int senderId;

  public GetMeMessageCommand() {

    setType(GET_ME_MESSAGE);
    setSenderId(-1);

  }

  public int getQueueId() {
    return queueId;
  }

  public void setQueueId(int queueId) {
    this.queueId = queueId;
  }

  public boolean isDelete() {
    return delete;
  }

  public void setDelete(boolean delete) {
    this.delete = delete;
  }

  public int getSenderId() {
    return senderId;
  }

  public void setSenderId(int senderId) {
    this.senderId = senderId;
  }

  @Override
  public String toString(){
    return "<cmd>\n" +
            "    <type>"+ getType() +"</type>\n" +
            "    <clientId>"+ getClientId() +"</clientId>\n" +
            "    <senderId>"+ getSenderId() +"</senderId>\n" +
            "    <queueId>"+ getQueueId() +"</queueId>\n" +
            "    <commandId>"+ getCommandId() +"</commandId>\n" +
            "    <delete>"+ isDelete() +"</delete>\n" +
            "    <commandSetId>"+ getCommandSetId() +"</commandSetId>\n" +
            "    <dateSend>"+getDateSend()+"</dateSend>\n" +
            "</cmd>";
  }
}
