package cn.answer;

/**
 * An error answer
 */
public class ErrorAnswer extends Answer{

  public ErrorAnswer(String message){
    setMessage(message);
    setType(Answer.ERROR);
  }

  @Override
  public String toString(){
    return "<ans>\n" +
            "    <type>"+getType()+"</type>\n" +
            "    <mes>"+getMessage()+"</mes>\n" +
            "</ans>";
  }

}
