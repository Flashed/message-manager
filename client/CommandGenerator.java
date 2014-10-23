import server.command.Command;

/**
 * Генератор комманд
 */
public class CommandGenerator {

  public String getCreateQueueCommand(){
    return "<cmd>" +
              "<type>"+ Command.CREATE_QUEUE+"</type>" +
            "</cmd>";
  }

}
