package cn.answer;

/**
 * Answer - message
 */
public class MessageAnswer extends Answer{

  private String text;

  public MessageAnswer() {
    setType(MESSAGE);
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString(){
    return "<ans>\n" +
            "    <type>"+ getType() +"</type>\n" +
            "    <mes>"+getMessage()+"</mes>\n" +
            "    <dateSend>"+getDateSend()+"</dateSend>\n" +
            "    <text>"+getText()+"</text>\n" +
            "    <timeOfReceiptServer>"+getTimeOfReceiptServer()+"</timeOfReceiptServer>\n" +
            "    <timeOfExecSql>"+getTimeOfExecSql()+"</timeOfExecSql>\n" +
            "    <timeOfExecuteServer>"+getTimeOfExecuteServer()+"</timeOfExecuteServer>\n" +
            "    <dateAnswer>"+getDateAnswer()+"</dateAnswer>\n" +
            "</ans>";
  }
}
