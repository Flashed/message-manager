import cn.answer.Answer;

import java.nio.ByteBuffer;

/**
 * @author Mikhail Zaitsev
 */
public interface ReadListener {

  void readBByteBuffer(Answer answer);
}
