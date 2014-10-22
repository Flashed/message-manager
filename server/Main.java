package manager.server;

import java.io.IOException;

/**
 * Created by User on 17.10.2014.
 */
public class Main {

    public static void main(String... args) throws IOException {
      System.out.println("Application started.");
      Server messageManager = new Server(4463, 2);
      messageManager.init();
      messageManager.start();

    }


}
