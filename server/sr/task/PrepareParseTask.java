package sr.task;

import cn.command.Command;
import sr.context.AppContext;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mikhail Zaitsev
 */
public class PrepareParseTask implements Runnable{

  private static final Logger logger = Logger.getLogger(PrepareParseTask.class.getName());

  private SocketChannel clientChanel;

  private long startExecTime;

  public PrepareParseTask(SocketChannel clientChanel) {
    this.clientChanel = clientChanel;
    this.startExecTime = System.currentTimeMillis();
  }

  @Override
  public void run() {
    try{
      StringBuilder buffer = getBuffer();
      String cmd = null;
      synchronized (buffer){
        while(checkBuffer(buffer)){
          cmd = preParseCommand(buffer);
          if(cmd == null){
            logger.warning("Failed to pre-parse command. But buffer is checked." +
                      " Buffer:\n " + buffer.toString());
          }
        }
      }
      if(cmd == null){
        return;
      }
      if(logger.isLoggable(Level.FINE)){
        logger.fine("Pre-parsed: \n" + cmd);
      }

      getExecutor().execute(new ParseCommandTask(cmd, clientChanel, startExecTime));
      if(logger.isLoggable(Level.FINE)){
        logger.fine("Size of queue of tasks: " + getExecutor().getQueue().size());
      }

    } catch (Exception e){
      logger.log(Level.SEVERE,
              "Failed to pre-parse command. Buffer: \n" + getBuffer().toString(), e);
    }
  }

  private String preParseCommand(StringBuilder buffer){
    String data = buffer.toString();
    int start = data.indexOf("<cmd>");
    int end = data.indexOf("</cmd>");
    String cmd = data.substring(start + 5);
    cmd = cmd.substring(0, cmd.indexOf("</cmd>"));
    String type = cmd.substring(cmd.indexOf("<type>") + 6);
    type = type.substring(0, type.indexOf("</type>"));

    if(Command.getTypesSet().contains(type)){
      end = end + 6;
      buffer.delete(start, end);
      if(logger.isLoggable(Level.FINE)){
        logger.fine("Deleted from charBuffer: \n" + data.substring(start, end));
      }
      return cmd;
    }
    return null;
  }

  private boolean checkBuffer(StringBuilder buffer){
    String data = buffer.toString();
    if(data.contains("<cmd>") && data.contains("</cmd>")){
      if(data.contains("<type>") && data.contains("</type>")){
        return true;
      }
    }
    return false;
  }


  private StringBuilder getBuffer(){
    synchronized (AppContext.getAppContext().getCommandBuffers()){
      if(!AppContext.getAppContext().getCommandBuffers().containsKey(clientChanel)){
        AppContext.getAppContext().getCommandBuffers().put(clientChanel, new StringBuilder());
      }
      return AppContext.getAppContext().getCommandBuffers().get(clientChanel);
    }
  }


  private ThreadPoolExecutor getExecutor(){
    return AppContext.getAppContext().getPoolExecutor();
  }

}
