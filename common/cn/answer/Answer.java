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

  private long timeAllHandle;

  private long timeNetTrans;

  private long timeOfExecSql;

  private long timeOfExecuteServer;

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

  public long getTimeAllHandle() {
    return timeAllHandle;
  }

  public void setTimeAllHandle(long timeAllHandle) {
    this.timeAllHandle = timeAllHandle;
  }

  public long getTimeNetTrans() {
    return timeNetTrans;
  }

  public void setTimeNetTrans(long timeNetTrans) {
    this.timeNetTrans = timeNetTrans;
  }

  @Override
  public String toString() {
    return  "    <type>"+ getType() +"</type>\n" +
            "    <mes>"+getMessage()+"</mes>\n" +
            "    <commandId>"+ getCommandId()+"</commandId>\n" +
            "    <dateSend>"+getDateSend()+"</dateSend>\n" +
            "    <timeAllHandle>"+getTimeAllHandle()+"</timeAllHandle>\n" +
            "    <timeNetTrans>"+getTimeAllHandle()+"</timeNetTrans>\n" +
            "    <timeOfExecSql>"+getTimeOfExecSql()+"</timeOfExecSql>\n" +
            "    <timeOfExecuteServer>"+getTimeOfExecuteServer()+"</timeOfExecuteServer>\n" +
            "    <commandSetId>"+getCommandSetId()+"</commandSetId>\n";
  }

  public static void setTimeServerToAnswer(Command command, Answer answer, long startExecDate, long endExecSqlTime){
    answer.setCommandId(command.getCommandId());
    answer.setDateSend(command.getDateSend());
    answer.setTimeOfExecSql(endExecSqlTime);
    answer.setTimeOfExecuteServer(System.currentTimeMillis() - startExecDate);
  }

  public static void setTimeAllAndNetTransToAnswer(Answer answer){
    long dateSend = answer.getDateSend();
    long allTimeHandle = System.currentTimeMillis() -dateSend;
    long timeNetTrans = allTimeHandle - answer.getTimeOfExecuteServer();
    answer.setTimeNetTrans(timeNetTrans);
    answer.setTimeAllHandle(allTimeHandle);


  }


}
