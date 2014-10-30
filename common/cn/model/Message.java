package cn.model;

/**
 * Message
 */
public class Message {

  private int id;

  private int queueId;

  private int senderId;

  private int receiverId;

  private String text;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getQueueId() {
    return queueId;
  }

  public void setQueueId(int queueId) {
    this.queueId = queueId;
  }

  public int getSenderId() {
    return senderId;
  }

  public void setSenderId(int senderId) {
    this.senderId = senderId;
  }

  public int getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(int receiverId) {
    this.receiverId = receiverId;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return "Message{" +
            "id=" + id +
            ", queueId=" + queueId +
            ", senderId=" + senderId +
            ", receiverId=" + receiverId +
            '}';
  }
}
