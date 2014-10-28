import cn.command.Command;

/**
 * @author Mikhail Zaitsev
 */
public interface ExecutorListener {

  void onGetCommand(Command command);

}
