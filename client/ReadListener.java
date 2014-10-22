import java.nio.ByteBuffer;

/**
 * @author Mikhail Zaitsev
 */
public interface ReadListener {

  void readBByteBuffer(ByteBuffer byteBuffer);
}
