package cn.command;

/**
 * The abstract sr.cn.command
 */
public abstract class Command {

  public static final String CREATE_QUEUE = "create_queue";
  public static final String QUEUE_LIST = "queue_list";
  public static final String CLIENT_LIST = "client_list";
  public static final String REGISTER_CLIENT = "register_client";
  public static final String SEND_MESSAGE = "send_message";
  public static final String GET_ME_MESSAGE = "get_me_message";


  private String type;

  private int clientId;

  private long commandId;

  private long dateSend;

  private long dateRecipient;

  private int commandSetId;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public long getCommandId() {
    return commandId;
  }

  public void setCommandId(long commandId) {
    this.commandId = commandId;
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

  public int getCommandSetId() {
    return commandSetId;
  }

  public void setCommandSetId(int commandSetId) {
    this.commandSetId = commandSetId;
  }

  public long getDateSend() {
    return dateSend;
  }

  public void setDateSend(long dateSend) {
    this.dateSend = dateSend;
  }
}
