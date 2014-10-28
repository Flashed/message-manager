package cn.answer;

public class Answer {

  public static final String SUCCESS = "success";
  public static final String ERROR = "error";
  public static final String QUEUES_LIST = "queues_list";


  private String message;

  private String type;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
