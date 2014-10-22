/**
 * @author Mikhail Zaitsev
 */
public class Main {

  public static void main(String... args){
    Client client = new Client("localhost", 4463);
    client.connect();
  }
}
