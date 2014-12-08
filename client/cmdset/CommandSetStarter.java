package cmdset;

import cmdset.executor.CommandSetExecutorListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Read ant start command set
 */
public class CommandSetStarter implements CommandSetExecutorListener{

  private static final Logger logger = Logger.getLogger(CommandSetStarter.class.getName());

  private CommandSetStarterListener listener;

  private List<CommandSet> cmdExecList = new ArrayList<>();

  private Iterator<CommandSet> iterator;

  private long timeoutExec;

  private volatile boolean started;

  private CommandSet current;


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

    if(started){
      return;
    }
    
    started = true;
    
    if(cmdExecList == null || cmdExecList.isEmpty()){
      logger.severe("Failed to start command set. List commandSets is null or empty!");
      return;
    }
    iterator = cmdExecList.iterator();

    current = iterator.next();
    current.setCurrentIteration(1);
    current.setId(0);
    runCommandSetIteration();
    
  }

  public void stop(){
    started = false;
  }

  @Override
  public void onFinished() {   
    if(!started){
      return;
    }
    
    if(current.getExecCount() > current.getCurrentIteration()){
      current.setCurrentIteration(current.getCurrentIteration() + 1);
      current.setId(current.getId() + 1);
      runCommandSetIteration();
    }else{
      if(!iterator.hasNext()){
        if(listener != null){
          listener.onStopCommandSetStarter();
        }
        return;
      }
      current = iterator.next();
      current.setCurrentIteration(current.getCurrentIteration() + 1);
      current.setId(current.getId() + 1);
      runCommandSetIteration();
    }
  }

  private void runCommandSetIteration() {

    if (listener != null) {
      logger.info(String.format("Execute  %s iteration %s", current.getType(), current.getCurrentIteration()));
      try {
        Thread.sleep(timeoutExec);
      } catch (InterruptedException ignore) {
      }
      listener.onGetCommandSet(current);
    }
  }
}
