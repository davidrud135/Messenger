package messenger;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
/**
 *
 * @author David Rudenko
 */
public class Coder {
  private static final String ALGO = "AES";
  private static final String KEY_STR = "TheBestSecretKey";

  public static String encrypt(String strToEncrypt) {
    String encryptedStr = "";
    try {
      Key key = generateKey();
      Cipher cipher = Cipher.getInstance(ALGO);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] encryptedBytes = cipher.doFinal(strToEncrypt.getBytes());
      encryptedStr = new String(Base64.getEncoder().encode(encryptedBytes));
    } catch (Exception ex) {
      System.out.println("Cant encrypt string.");
      ex.printStackTrace();
    }
    return encryptedStr;
  }

  public static String decrypt(String strToDecrypt) {
    String decryptedStr = "";
    try { 
      Key key = generateKey();
      Cipher cipher = Cipher.getInstance(ALGO);
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] decodedStrBytes = Base64.getDecoder().decode(strToDecrypt);
      byte[] decryptedBytes = cipher.doFinal(decodedStrBytes);
      decryptedStr = new String(decryptedBytes);
    } catch (Exception ex) {
      System.out.println("Cant decrypt string.");
      ex.printStackTrace();
    }
    return decryptedStr;
  }


  private static Key generateKey() throws Exception {
   return new SecretKeySpec(KEY_STR.getBytes(), ALGO);
  }
  
}
