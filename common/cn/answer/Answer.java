package cn.answer;

import cn.command.Command;

public class Answer {

  public static final String SUCCESS = "success";
  public static final String ERROR = "error";
  public static final String QUEUES_LIST = "queues_list";
  public static final String CLIENTS_LIST = "clients_list";
  public static final String MESSAGE = "message";


  private String message;

  private String type;

  private long commandId;

  private long dateSend;

  private long timeOfReceiptServer;

  private long timeOfReceiptClient;

  private long timeOfExecSql;

  private long timeOfExecuteServer;

  private long dateAnswer;

  private int commandSetId;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

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

  public long getTimeOfReceiptServer() {
    return timeOfReceiptServer;
  }

  public void setTimeOfReceiptServer(long timeOfReceiptServer) {
    this.timeOfReceiptServer = timeOfReceiptServer;
  }

  public long getTimeOfExecSql() {
    return timeOfExecSql;
  }

  public void setTimeOfExecSql(long timeOfExecSql) {
    this.timeOfExecSql = timeOfExecSql;
  }

  public long getTimeOfExecuteServer() {
    return timeOfExecuteServer;
  }

  public void setTimeOfExecuteServer(long timeOfExecuteServer) {
    this.timeOfExecuteServer = timeOfExecuteServer;
  }

  public long getDateAnswer() {
    return dateAnswer;
  }

  public void setDateAnswer(long dateAnswer) {
    this.dateAnswer = dateAnswer;
  }

  public int getCommandSetId() {
    return commandSetId;
  }

  public void setCommandSetId(int commandSetId) {
    this.commandSetId = commandSetId;
  }

  public long getTimeOfReceiptClient() {
    return timeOfReceiptClient;
  }

  public void setTimeOfReceiptClient(long timeOfReceiptClient) {
    this.timeOfReceiptClient = timeOfReceiptClient;
  }

  public long getDateSend() {
    return dateSend;
  }

  public void setDateSend(long dateSend) {
    this.dateSend = dateSend;
  }

  public static void setTimeToAnswer(Command command, Answer answer, long startExecTime, long endExecSqlTime){
    answer.setCommandId(command.getCommandId());
    answer.setDateSend(command.getDateSend());
    answer.setTimeOfReceiptServer(command.getDateRecipient()-command.getDateSend());
    answer.setTimeOfExecSql(endExecSqlTime);
    answer.setTimeOfExecuteServer(System.currentTimeMillis() - startExecTime);
    answer.setDateAnswer(System.currentTimeMillis());
  }
}
