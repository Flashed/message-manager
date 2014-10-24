package sr;

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

  public static int getPort(){
    try{
      return Integer.valueOf(getProp("server.port"));
    } catch (Exception e){
      return 0;
    }
  }

  public static int getNumberThread(){
    try{
      return Integer.valueOf(getProp("server.threads"));
    } catch (Exception e){
      return 0;
    }
  }

  public static int getDBConnectionsNumber(){
    try{
      return Integer.valueOf(getProp("server.datasource.connections"));
    } catch (Exception e){
      return 0;
    }
  }

  public static String getDBHost(){
    return getProp("server.datasource.host");
  }

  public static String getDBUsername(){
    return getProp("server.datasource.username");
  }

  public static String getDBPassword(){
    return getProp("server.datasource.password");
  }

  public static String getDBName(){
    return getProp("server.datasource.db");
  }

  private static String getProp(String key){
    return props.getProperty(key);
  }

}
