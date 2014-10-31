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

  private long dateSend;

  private long timeOfReceiptServer;

  private long timeOfExecSql;

  private long timeOfExecuteServer;

  private long dateAnswer;

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

  public long getDateSend() {
    return dateSend;
  }

  public void setDateSend(long dateSend) {
    this.dateSend = dateSend;
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

  public static void setTimeToAnswer(Command command, Answer answer, long startExecTime, long endExecSqlTime){
    answer.setDateSend(command.getDateSend());
    answer.setTimeOfReceiptServer(command.getDateRecipient()-command.getDateSend());
    answer.setTimeOfExecSql(endExecSqlTime);
    answer.setTimeOfExecuteServer(System.currentTimeMillis() - startExecTime);
    answer.setDateAnswer(System.currentTimeMillis());
  }
}
