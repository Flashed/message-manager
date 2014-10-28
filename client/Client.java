import cn.answer.Answer;
import cn.command.Command;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Mikhail Zaitsev
 */
public class Client implements  ReadListener, ExecutorListener{

  private static final Logger logger = Logger.getLogger(Client.class.getName());

  private String host;

  private int port;

  private long timeoutExec;

  private SocketChannel socketChannel;

  public Client(String host, int port, long timeoutExec) {
    this.host = host;
    this.port = port;
    this.timeoutExec = timeoutExec;
  }

  public void connect(){
    try {
      socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
      new Thread(new TaskRead(socketChannel, this))
              .start();
      logger.info(String.format("Connected to %s:%s",host, port));
      startExecutor();
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error of connection", e);
    }
  }


  private void startExecutor(){
    CommandExecutor commandGenerator = new CommandExecutor(timeoutExec, this);
    commandGenerator.execute();
  }


  @Override
  public void readBByteBuffer(Answer answer) {
    System.out.println(answer);
  }


  public static void main(String... args){

    try {
      LogManager.getLogManager().readConfiguration(Client.class.getClassLoader().getResourceAsStream("logging.properties"));
    } catch (SecurityException | IOException e1) {
      logger.log(Level.SEVERE, "Error config logger", e1);
    }

    new Config().init();

    Client client = new Client(Config.getServerHost(),
            Config.getServerPort(),
            Config.getExecTimeout());
    client.connect();
  }

  @Override
  public void onGetCommand(Command command) {
    try{
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      buffer.put(command.toString().getBytes());
      buffer.flip();
      socketChannel.write(buffer);
      buffer.clear();
    }catch (Exception e){
      logger.log(Level.SEVERE, "Error send command ", e);
    }
  }
}
