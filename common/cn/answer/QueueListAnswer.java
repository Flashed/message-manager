package cn.answer;

import cn.model.Queue;

import java.util.List;

/**
 * @author Mikhail Zaitsev
 */
public class QueueListAnswer extends Answer{

  private List<Queue> queues;

  public QueueListAnswer(List<Queue> queues) {
    setType(QUEUES_LIST);
    setMessage("ids of queues");
    this.queues = queues;
  }


  @Override
  public String toString(){
    StringBuilder data = new StringBuilder();
    for (Queue queue : queues){
      data.append("<id>");
      data.append(queue.getId());
      data.append("</id>");
      data.append("\n");
    }

    return "<ans>\n" +
            "    <type>"+getType()+"</type>\n" +
            "    <mes>"+getMessage()+"</mes>\n" +
            "    <ids>\n" +
            "" + data.toString() + "\n" +
            "    </ids>\n" +
            "</ans>";
  }
}
