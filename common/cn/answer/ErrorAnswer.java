package cn.answer;

/**
 * An error answer
 */
public class ErrorAnswer extends Answer{

  public ErrorAnswer(String message){
    setMessage(message);
    setType(Answer.ERROR);
  }

  public ErrorAnswer() {
    setType(Answer.ERROR);
  }

  @Override
  public String toString(){
    return "<ans>\n" +
             super.toString() +
             "</ans>";
  }

}
