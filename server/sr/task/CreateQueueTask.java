package sr.task;

import sr.command.CreateQueueCommand;

/**
 * Task - create queue
 */
public class CreateQueueTask implements Runnable{

  private CreateQueueCommand command;

  public CreateQueueTask(CreateQueueCommand command) {
    this.command = command;
  }

  @Override
  public void run() {
    System.out.println(CreateQueueTask.class.getName());
  }
}
