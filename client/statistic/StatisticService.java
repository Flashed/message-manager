package statistic;

import cn.answer.Answer;


import java.io.*;
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

  private List<String> outList = new ArrayList<>();

  private String fileName = "./logs/statistic.txt";

  private long lastOutTime;

  private int clientId;

  public StatisticService() {
    try {
      FileOutputStream statisticFile = new FileOutputStream(fileName);
      statisticFile.write(String.format("%12s\t\t%14s\t\t%10s\t\t%28s\t\t%19s\t\t%14s\t\t%19s\r\n",
              "clientId",
              "commandSetType",
              "answerType",
              "dateSend",
              "timeOfReceiptServer",
              "timeOfExecSql",
              "timeOfExecuteServer").getBytes());
      statisticFile.flush();
      statisticFile.close();
      lastOutTime = System.currentTimeMillis();
    } catch (Exception e){
      logger.log(Level.SEVERE, "Failed to open statistic file");
    }
  }

  public void write(String commandSetType ,Answer answer){
    try {
      outList.add(String.format("%12s\t\t%14s\t\t%10s\t\t%28s\t\t%19s\t\t%14s\t\t%19s\n",
              clientId,
              commandSetType,
              answer.getType(),
              new Date(answer.getDateSend()),
              answer.getTimeOfReceiptServer(),
              answer.getTimeOfExecSql(),
              answer.getTimeOfExecuteServer()));
      if ((System.currentTimeMillis() - lastOutTime) > 10000) {
        writeToFile();
      }
    }catch (Exception e){
      logger.log(Level.SEVERE, "Failed to write statistic");
    }
  }

  private void writeToFile(){
    try {
      FileWriter writer = new FileWriter(fileName,true);
      for(String s :outList){
        writer.write(s);
      }
      writer.flush();
      writer.close();
      outList.clear();
      lastOutTime = System.currentTimeMillis();
      logger.info("Write statistic to " + fileName);
    } catch (Exception e){
      logger.log(Level.SEVERE, "Failed to write to statistic file");
    }

  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
  }
}
