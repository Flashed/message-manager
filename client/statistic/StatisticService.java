package statistic;

import cn.answer.Answer;

import java.util.logging.Logger;

/**
 * @author Mikhail Zaitsev
 */
public class StatisticService {

  private static final Logger logger = Logger.getLogger(StatisticService.class.getName());

  public void write(Answer answer){
    logger.info("Write statistic");
  }

}
