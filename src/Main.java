import java.io.IOException;

/**
 * Created by User on 17.10.2014.
 */
public class Main {

    public static void main(String... args) throws IOException {
      System.out.println("Application started.");
      MessageManager messageManager = new MessageManager(4463, 2);
      messageManager.init();
      messageManager.start();

    }


}
