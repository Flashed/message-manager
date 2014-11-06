package cn.command;

/**
 * The server cn.command - to create a queue
 */
public class CreateQueueCommand extends Command{

  private int queueId;

  public CreateQueueCommand() {
    setType(CREATE_QUEUE);
  }

  public int getQueueId() {
    return queueId;
  }

  public void setQueueId(int queueId) {
    this.queueId = queueId;
  }

  @Override
  public String toString() {
    return "<cmd>\n" +
            super.toString() +
            "    <queueId>"+ getQueueId() +"</queueId>\n" +
            "</cmd>";
  }
}
