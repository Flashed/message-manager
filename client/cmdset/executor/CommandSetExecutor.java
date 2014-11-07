package cmdset.executor;

import cmdset.CommandSet;
import cn.answer.Answer;
import read.ReadListener;

import java.util.Map;

/**
 * Executor of command set
 */
public interface CommandSetExecutor{

  void execute(CommandSet commandSet);

  void handleAnswer(Answer answer);

  void setHandlesTimesExecutorsMap(Map<Long,CommandSetExecutor> handlesTimesExecutorsMap);

  void setListener(CommandSetExecutorListener listener);

}
