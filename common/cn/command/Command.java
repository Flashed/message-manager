package cn.command;

/**
 * The abstract sr.cn.command
 */
public abstract class Command {

  public static final String CREATE_QUEUE = "create_queue";
  public static final String QUEUE_LIST = "queue_list";
  public static final String REGISTER_CLIENT = "register_client";


  private String type;

  private int clientId;

  private long dateSend;

  private long dateRecipient;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public long getDateSend() {
    return dateSend;
  }

  public void setDateSend(long dateSend) {
    this.dateSend = dateSend;
  }

  public long getDateRecipient() {
    return dateRecipient;
  }

  public int getClientId() {
    return clientId;
  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
  }

  public void setDateRecipient(long dateRecipient) {
    this.dateRecipient = dateRecipient;
  }
}
