package sr.command;

/**
 * The server command - to create a queue
 */
public class CreateQueueCommand extends Command{

  private int clientId;

  private int queueId;

  public CreateQueueCommand() {
  }

  public int getClientId() {
    return clientId;
  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
  }

  public int getQueueId() {
    return queueId;
  }

  public void setQueueId(int queueId) {
    this.queueId = queueId;
  }
}
