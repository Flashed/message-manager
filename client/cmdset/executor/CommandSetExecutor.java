package cmdset.executor;

import cmdset.CommandSet;
import read.ReadListener;

/**
 * Executor of command set
 */
public interface CommandSetExecutor extends ReadListener {

  public void execute(CommandSet commandSet);

}
