package sr.answer;

/**
 * An error answer
 */
public class ErrorAnswer extends Answer{

  public ErrorAnswer(String message){
    setMessage(message);
  }

  @Override
  public String toString(){
    return "<ans>\n" +
            "    <type>error</type>\n" +
            "    <mes>"+getMessage()+"</mes>\n" +
            "</ans>";
  }

}
