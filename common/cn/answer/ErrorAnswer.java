package cn.answer;

/**
 * An error answer
 */
public class ErrorAnswer extends Answer{

  public ErrorAnswer(String message){
    setMessage(message);
    setType(Answer.ERROR);
  }

  public ErrorAnswer() {
    setType(Answer.ERROR);
  }

  @Override
  public String toString(){
    return "<ans>\n" +
            "    <type>"+ getType() +"</type>\n" +
            "    <mes>"+getMessage()+"</mes>\n" +
            "    <dateSend>"+getDateSend()+"</dateSend>\n" +
            "    <timeOfReceiptServer>"+getTimeOfReceiptServer()+"</timeOfReceiptServer>\n" +
            "    <timeOfExecSql>"+getTimeOfExecSql()+"</timeOfExecSql>\n" +
            "    <timeOfExecuteServer>"+getTimeOfExecuteServer()+"</timeOfExecuteServer>\n" +
            "    <dateAnswer>"+getDateAnswer()+"</dateAnswer>\n" +
            "    <commandSetId>"+getCommandSetId()+"</commandSetId>\n" +
            "</ans>";
  }

}
