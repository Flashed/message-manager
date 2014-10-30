package cmdset;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Read ant start command set
 */
public class CommandSetStarter {

  private static final Logger logger = Logger.getLogger(CommandSetStarter.class.getName());

  private CommandSetStarterListener listener;

  private List<CommandSet> cmdExecList = new ArrayList<>();

  private long timeoutExec;

  public CommandSetStarter(long timeoutExec, CommandSetStarterListener listener) {
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
        CommandSet cmdExec = new CommandSet();
        if(chunks.length == 1){
          cmdExec.setType(chunks[0]);
          cmdExecList.add(cmdExec);
        } else if(chunks.length == 2){
          cmdExec.setType(chunks[0]);
          int c = 0;
          try{
            c = Integer.valueOf(chunks[1]);
          }catch (Exception ignore){}
          cmdExec.setExecCount(c == 0 ? 1 : c);
          cmdExecList.add(cmdExec);
        }
      }
    } catch (Exception e){
      logger.log(Level.SEVERE, "Error read inputCommands.txt", e);
    }
  }

  public void start(){

      for(CommandSet cmdExec: cmdExecList){
        for(int i=0; i<cmdExec.getExecCount(); i++){
          if(listener != null){
            listener.onGetCommandSet(cmdExec);
            logger.info(String.format("Execute  %s iteration %s", cmdExec.getType(), i+1));
            try {
              Thread.sleep(timeoutExec);
            } catch (InterruptedException ignore) {
            }
          }
        }
      }


  }

}
