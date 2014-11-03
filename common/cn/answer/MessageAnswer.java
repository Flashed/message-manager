package cn.answer;

/**
 * Answer - message
 */
public class MessageAnswer extends Answer{

  private String text;

  private int messageId;

  public MessageAnswer() {
    setType(MESSAGE);
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getMessageId() {
    return messageId;
  }

  public void setMessageId(int messageId) {
    this.messageId = messageId;
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
            "    <commandSetId>"+getCommandSetId()+"</commandSetId>\n" +
            "    <messageId>"+getMessageId()+"</messageId>\n" +
            "</ans>";
  }
}
