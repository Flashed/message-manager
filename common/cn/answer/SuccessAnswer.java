package cn.answer;

/**
 * A success answer
 */
public class SuccessAnswer extends Answer{

  public SuccessAnswer(String message) {
    setMessage(message);
    setType(Answer.SUCCESS);
  }

  public SuccessAnswer() {
    setType(Answer.SUCCESS);
  }

  @Override
  public String toString(){
    return "<ans>\n" +
            super.toString() +
            "</ans>";
  }

}
