package cn.command;

/**
 * Command send message
 */
public class SendMessageCommand extends Command{

  private String text;

  private int recipientId;

  private int queueId;

  public SendMessageCommand() {
    setType(Command.SEND_MESSAGE);
    setRecipientId(-1);
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(int recipientId) {
    this.recipientId = recipientId;
  }

  public int getQueueId() {
    return queueId;
  }

  public void setQueueId(int queueId) {
    this.queueId = queueId;
  }

  @Override
  public String toString() {
    return "<cmd>\n" +
            "    <type>" + getType() + "</type>\n" +
            "    <recipientId>"+ getRecipientId() +"</recipientId>\n" +
            "    <queueId>"+ getQueueId() +"</queueId>\n" +
            "    <clientId>"+ getClientId() +"</clientId>\n" +
            "    <dateSend>"+ getDateSend() +"</dateSend>\n" +
            "    <text>"+ getText() +"</text>\n" +
            "    <commandSetId>"+ getCommandSetId() +"</commandSetId>\n" +
            "</cmd>";
  }
}
