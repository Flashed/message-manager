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
            super.toString() +
            "    <text>"+getText()+"</text>\n" +
            "    <messageId>"+getMessageId()+"</messageId>\n" +
            "</ans>";
  }
}
