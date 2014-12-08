package read;

import cn.answer.Answer;

/**
 */
public interface ReadListener {

  void onReadAnswer(Answer answer);

  void onStopRead();
}
