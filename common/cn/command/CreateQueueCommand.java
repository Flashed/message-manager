package cn.command;

/**
 * The server cn.command - to create a queue
 */
public class CreateQueueCommand extends Command{

  private int clientId;

  private int queueId;

  public CreateQueueCommand() {
    setType(CREATE_QUEUE);
  }

  public int getClientId() {
    return clientId;
  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
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
            "    <type>"+ getType() +"</type>\n" +
            "    <clientId>"+ getClientId() +"</clientId>\n" +
            "    <queueId>"+ getQueueId() +"</queueId>\n" +
            "    <dateSend>"+ getDateSend() +"</dateSend>\n" +
            "</cmd>";
  }
}
