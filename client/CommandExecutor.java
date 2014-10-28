import cn.command.Command;
import cn.command.CreateQueueCommand;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Генератор команд
 */
public class CommandExecutor {

  private static final Logger logger = Logger.getLogger(CommandExecutor.class.getName());

  private ExecutorListener listener;

  private List<CmdExec> cmdExecList = new ArrayList<>();

  private long timeoutExec;

  public CommandExecutor(long timeoutExec, ExecutorListener listener) {
    if(timeoutExec <= 0){
      this.timeoutExec = 10;
    }else{
      this.timeoutExec = timeoutExec;
    }
    readAndParseInput();
    this.listener = listener;
  }

  private void readAndParseInput(){

    try(
          InputStream inputStream = getClass().getClassLoader()
            .getResourceAsStream("inputCommands.txt");
          BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    ){
      String line = null;
      while ((line = reader.readLine()) != null) {
        if(line.isEmpty()){
          continue;
        }
        String[] chunks = line.split(" ");
        if(chunks == null){
          continue;
        }
        CmdExec cmdExec = new CmdExec();
        if(chunks.length == 1){
          cmdExec.setCommandType(chunks[0]);
          cmdExecList.add(cmdExec);
        } else if(chunks.length == 2){
          cmdExec.setCommandType(chunks[0]);
          int c = 0;
          try{
            c = Integer.valueOf(chunks[1]);
          }catch (Exception ignore){}
          cmdExec.setCount(c==0 ? 1 : c);
          cmdExecList.add(cmdExec);
        }
      }
    } catch (Exception e){
      logger.log(Level.SEVERE, "Error read inputCommands.txt", e);
    }
  }

  public void execute(){

      for(CmdExec cmdExec: cmdExecList){
        for(int i=0; i<cmdExec.getCount(); i++){
          if(listener != null){
            if(Command.CREATE_QUEUE.equals(cmdExec.getCommandType())){
              listener.onGetCommand(createCreateQueueCommand());
            }
            logger.info(String.format("Execute  %s iteration %s", cmdExec.getCommandType(), i+1));
            try {
              Thread.sleep(timeoutExec);
            } catch (InterruptedException ignore) {
            }
          }
        }
      }


  }

  private CreateQueueCommand createCreateQueueCommand(){
    CreateQueueCommand cmd = new CreateQueueCommand();
    cmd.setQueueId((int)(Math.random()*100000));
    return cmd;
  }

}
