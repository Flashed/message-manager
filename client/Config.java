import java.io.InputStream;
import java.util.Properties;

/**
 * Properties of configuration
 */
public class Config {

  private static Properties props;

  public void init(){
    try{

      InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
      props = new Properties();
      props.load(inputStream);

    } catch (Exception e){
      props = System.getProperties();
    }
  }

  public static int getServerPort(){
    try{
      return Integer.valueOf(getProp("server.port"));
    } catch (Exception e){
      return 0;
    }
  }

  public static String getServerHost(){
      return getProp("server.host");
  }

  public static int getExecTimeout(){
    try{
      return Integer.valueOf(getProp("client.exec_timeout"));
    } catch (Exception e){
      return 0;
    }
  }

  public static String getStatisticFolder(){
    try{
      return getProp("client.statistic_folder");
    } catch (Exception e){
      return "";
    }
  }

  public static int getClientId(){
    try{
      return Integer.valueOf(getProp("client.id"));
    } catch (Exception e){
      return (int) (Math.random() *100000);
    }
  }

  private static String getProp(String key){
    return props.getProperty(key);
  }

}
