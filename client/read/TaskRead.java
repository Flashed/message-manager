package read;

import cn.answer.*;
import cn.model.Queue;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
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

  public TaskRead(SocketChannel socketChannel) {
    this.socketChannel = socketChannel;
  }

  @Override
  public void run() {

    if(socketChannel == null){
      return;
    }

    try{

      int num;
      while((num = socketChannel.read(readBuffer)) != -1){
        writeToCharBuffer();
        while(checkBuffer()){
          if(readListener != null){
            try{
              Answer answer = parseAnswer();
              readListener.onReadAnswer(answer);
            } catch (Exception e){
              logger.log(Level.SEVERE, "Failed to read answer \n " + new String(readBuffer.array()) , e);
            }
          }
          readBuffer.clear();
        }
      }
      socketChannel.close();
    } catch (Exception e){
      logger.log(Level.SEVERE, "Reading was stop" , e);
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
    int start = data.indexOf("<ans>");
    int end = data.indexOf("</ans>");
    String answer = data.substring(start + 5);
    answer = answer.substring(0, answer.indexOf("</ans>"));
    String type = answer.substring(answer.indexOf("<type>") + 6);
    type = type.substring(0, type.indexOf("</type>"));

    if(Answer.SUCCESS.equals(type)){
      charBuffer.delete(start, end+6);
      return parseSuccessAnswer(answer);
    } else if (Answer.ERROR.equals(type)){
      charBuffer.delete(start, end+6);
      return parseErrorAnswer(answer);
    } if(Answer.QUEUES_LIST.equals(type)){
      charBuffer.delete(start, end+6);
      return parseQueueListAnswer(answer);
    } if(Answer.MESSAGE.equals(type)){
      charBuffer.delete(start, end+6);
      return parseMessageAnswer(answer);
    }

    return null;
  }

  private MessageAnswer parseMessageAnswer(String data){
    try{
      String text = data.substring(data.indexOf("<text>") + 6);
      text = text.substring(0, text.indexOf("</text>"));
      MessageAnswer answer = new MessageAnswer();
      answer.setText(text);
      parseCommonFields(answer, data);
      return answer;

    }catch (Exception e){
      logger.log(Level.SEVERE, "Error parse CreateQueueCommand" , e);
    }
    return null;
  }

  private QueueListAnswer parseQueueListAnswer(String data){
    try{

      String ids = data.substring(data.indexOf("<ids>") + 5);
      ids = ids.substring(0, ids.indexOf("</ids>"));

      List<Queue> queues = new ArrayList<>();

      String id;
      try {
        while(true){
          id= ids.substring(ids.indexOf("<id>") + 4, ids.indexOf("</id>"));
          ids = ids.substring(ids.indexOf("</id>") + 5, ids.length() );
          Queue queue = new Queue();
          queue.setId(Integer.valueOf(id));
          queues.add(queue);
        }
      } catch (IndexOutOfBoundsException ignore){}

      QueueListAnswer answer = new QueueListAnswer(queues);
      parseCommonFields(answer, data);
      return answer;

    }catch (Exception e){
      logger.log(Level.SEVERE, "Error parse CreateQueueCommand" , e);
    }
    return null;
  }


  private SuccessAnswer parseSuccessAnswer(String data){
    try{
      SuccessAnswer answer = new SuccessAnswer();
      parseCommonFields(answer, data);
      return answer;

    }catch (Exception e){
      logger.log(Level.SEVERE, "Error parse CreateQueueCommand" , e);
    }
    return null;
  }

  private ErrorAnswer parseErrorAnswer(String data){
    try{
      ErrorAnswer answer = new ErrorAnswer();
      parseCommonFields(answer, data);
      return answer;
    }catch (Exception e){
      logger.log(Level.SEVERE, "Error parse CreateQueueCommand" , e);
    }
    return null;
  }

  private void parseCommonFields(Answer answer, String data){
    String mes = data.substring(data.indexOf("<mes>") + 5);
    mes = mes.substring(0, mes.indexOf("</mes>"));
    String dateSend = data.substring(data.indexOf("<dateSend>") + 10);
    dateSend = dateSend.substring(0, dateSend.indexOf("</dateSend>"));
    String timeOfReceiptServer = data.substring(data.indexOf("<timeOfReceiptServer>") + 21);
    timeOfReceiptServer = timeOfReceiptServer.substring(0, timeOfReceiptServer.indexOf("</timeOfReceiptServer>"));
    String timeOfExecSql = data.substring(data.indexOf("<timeOfExecSql>") + 15);
    timeOfExecSql = timeOfExecSql.substring(0, timeOfExecSql.indexOf("</timeOfExecSql>"));
    String timeOfExecuteServer = data.substring(data.indexOf("<timeOfExecuteServer>") + 21);
    timeOfExecuteServer = timeOfExecuteServer.substring(0, timeOfExecuteServer.indexOf("</timeOfExecuteServer>"));
    String dateAnswer = data.substring(data.indexOf("<dateAnswer>") + 12);
    dateAnswer = dateAnswer.substring(0, dateAnswer.indexOf("</dateAnswer>"));

    answer.setMessage(mes);
    answer.setDateSend(Long.valueOf(dateSend));
    answer.setTimeOfReceiptServer(Long.valueOf(timeOfReceiptServer));
    answer.setTimeOfExecSql(Long.valueOf(timeOfExecSql));
    answer.setTimeOfExecuteServer(Long.valueOf(timeOfExecuteServer));
    answer.setDateAnswer(Long.valueOf(dateAnswer));

  }

  private void writeToCharBuffer(){
    readBuffer.flip();
    byte[] bytes = readBuffer.array();
    for(int i = readBuffer.position(); i<readBuffer.limit(); i++){
      if(charBuffer.length() > (100*1000*1024)){
        charBuffer.setLength(0);
        break;
      }
      charBuffer.append((char)bytes[i]);
    }
    readBuffer.clear();
  }


  public void setReadListener(ReadListener readListener) {
    this.readListener = readListener;
  }

  public ReadListener getReadListener() {
    return readListener;
  }
}
