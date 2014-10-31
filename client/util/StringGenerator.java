package util;

/**
 * Generate string
 */
public class StringGenerator {

  private static final char[] characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890\n\r".toCharArray();

  public static String generate(int length){
    int charactersLength = characters.length;
     StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < length; i++) {
      double index = Math.random() * (charactersLength -1);
      buffer.append(characters[(int)index]);
    }
    return buffer.toString();
  }
}
