import cn.answer.Answer;
import cn.answer.ErrorAnswer;
import cn.answer.SuccessAnswer;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mikhail Zaitsev
 */
public class TaskRead implements Runnable{

  private static final Logger logger = Logger.getLogger(TaskRead.class.getName());

  private SocketChannel socketChannel;

  private ReadListener readListener;

  private ByteBuffer readBuffer = ByteBuffer.allocate(1024);

  private StringBuilder charBuffer = new StringBuilder();

  public TaskRead(SocketChannel socketChannel, ReadListener readListener) {
    this.socketChannel = socketChannel;
    this.readListener = readListener;
  }

  @Override
  public void run() {

    if(readListener == null || socketChannel == null){
      return;
    }

    try{

      int num;
      while((num = socketChannel.read(readBuffer)) != -1){
        writeToCharBuffer();
        if(checkBuffer()){
          Answer answer = parseAnswer();
          readListener.readBByteBuffer(answer);
          readBuffer.clear();
          charBuffer.setLength(0);
        }
      }
      socketChannel.close();
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  private boolean checkBuffer(){
    String data = charBuffer.toString();
    if(data.contains("<ans>") && data.contains("</ans>")){
      if(data.contains("<type>") && data.contains("</type>")){
        return true;
      }
    }
    return false;
  }

  private Answer parseAnswer(){
    String data = charBuffer.toString();
    data = data.substring(data.indexOf("<ans>") + 5);
    data = data.substring(0, data.indexOf("</ans>"));
    String type = data.substring(data.indexOf("<type>") + 6);
    type = type.substring(0, type.indexOf("</type>"));

    if(Answer.SUCCESS.equals(type)){
      return parseSuccessAnswer(data);
    } else if (Answer.ERROR.equals(type)){
      return parseErrorAnswer(data);
    }

    return null;
  }

  private SuccessAnswer parseSuccessAnswer(String data){
    try{

      String mes = data.substring(data.indexOf("<mes>") + 5);
      mes = mes.substring(0, mes.indexOf("</mes>"));

      return new SuccessAnswer(mes);

    }catch (Exception e){
      logger.log(Level.SEVERE, "Error parse CreateQueueCommand" , e);
    }
    return null;
  }

  private ErrorAnswer parseErrorAnswer(String data){
    try{

      String mes = data.substring(data.indexOf("<mes>") + 5);
      mes = mes.substring(0, mes.indexOf("</mes>"));

      return new ErrorAnswer(mes);

    }catch (Exception e){
      logger.log(Level.SEVERE, "Error parse CreateQueueCommand" , e);
    }
    return null;
  }

  private void writeToCharBuffer(){
    readBuffer.flip();
    byte[] bytes = readBuffer.array();
    for(int i = readBuffer.position(); i<readBuffer.limit(); i++){
      if(charBuffer.length() > (5*1024)){
        charBuffer.setLength(0);
        break;
      }
      charBuffer.append((char)bytes[i]);
    }
    readBuffer.clear();
  }


}
