package cn.answer;

/**
 * A success answer
 */
public class SuccessAnswer extends Answer{

  public SuccessAnswer(String message) {
    setMessage(message);
  }

  @Override
  public String toString(){
    return "<ans>\n" +
            "    <type>success</type>\n" +
            "    <mes>"+getMessage()+"</mes>\n" +
            "</ans>";
  }

}
