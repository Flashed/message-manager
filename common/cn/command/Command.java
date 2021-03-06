package cn.command;

import java.util.HashSet;
import java.util.Set;

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

  private static final Set<String> typesSet = new HashSet<>();

  static {
    typesSet.add(CREATE_QUEUE);
    typesSet.add(QUEUE_LIST);
    typesSet.add(CLIENT_LIST);
    typesSet.add(REGISTER_CLIENT);
    typesSet.add(SEND_MESSAGE);
    typesSet.add(GET_ME_MESSAGE);

  }

  private String type;

  private int clientId;

  private long commandId;

  private long dateSend;

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

  public int getClientId() {
    return clientId;
  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
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

  public static Set<String> getTypesSet() {
    return typesSet;
  }

  @Override
  public String toString() {
    return  "    <type>" + getType() + "</type>\n" +
            "    <clientId>"+ getClientId() +"</clientId>\n" +
            "    <commandId>"+ getCommandId() +"</commandId>\n" +
            "    <commandSetId>"+ getCommandSetId() +"</commandSetId>\n" +
            "    <dateSend>"+getDateSend()+"</dateSend>\n" ;
  }
}
