package statistic;

import cn.answer.Answer;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Statistic service
 */
public class StatisticService {

  private static final Logger logger = Logger.getLogger(StatisticService.class.getName());

  private static final String STATISTIC_FORMAT = "%12s\t\t%31s\t\t%31s\t\t%28s\t\t%20s\t\t%18s\t\t%22s\t\t%14s\r\n";

  private static final String DATE_FORMAT = "YYYY-MM-dd HH:mm:ss.SSS";

  private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

  private List<String> outList = new ArrayList<>();

  private String fileName;

  private String folder;

  private int clientId;

  public StatisticService() {

  }

  public void init(){
    try {
      fileName = "/statistic_"+ clientId +".txt";
      FileWriter statisticFile = new FileWriter(folder + fileName, true);
      statisticFile.write(String.format(STATISTIC_FORMAT,
              "clientId",
              "commandSetId",
              "answerType",
              "dateSend",
              "timeNetworkTrans",
              "timeExecuteSql",
              "timeExecuteServer",
              "timeCommon"
              ));
      statisticFile.flush();
      statisticFile.close();

      new Thread(new Runnable() {
        @Override
        public void run() {
          while (true){
            try {
              Thread.sleep(10000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            writeToFile();
          }
        }
      }).start();
    } catch (Exception e){
      logger.log(Level.SEVERE, "Failed to open statistic file", e);
    }
  }

  public void write(String commandSetType ,Answer answer){
    try {
      synchronized (outList){
        outList.add(String.format(STATISTIC_FORMAT,
                clientId,
                commandSetType + "_" + answer.getCommandSetId(),
                answer.getType(),
                simpleDateFormat.format(new Date(answer.getDateSend())),
                answer.getTimeNetTrans(),
                answer.getTimeOfExecSql(),
                answer.getTimeOfExecuteServer(),
                answer.getTimeAllHandle()));
      }
    }catch (Exception e){
      logger.log(Level.SEVERE, "Failed to write statistic");
    }
  }

  private void writeToFile(){
    try {
      synchronized (outList){
        if(!outList.isEmpty() ){
          FileWriter writer = new FileWriter(folder+fileName,true);
          for(String s :outList){
            writer.write(s);
          }
          writer.flush();
          writer.close();
          outList.clear();
          logger.info("Write statistic to " + folder+fileName);
        }
      }
    } catch (Exception e){
      logger.log(Level.SEVERE, "Failed to write to statistic file", e);
    }

  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
  }

  public void setFolder(String folder) {
    this.folder = folder;
  }
}
