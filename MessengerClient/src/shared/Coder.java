package shared;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Class for encryption and decryption data using AES algorithm.
 * @author David Rudenko
 */
public class Coder {
  private static final String ALGO = "AES";
  private static final String KEY_STR = "TheBestSecretKey";

  /**
   * Encrypts string.
   * @param strToEncrypt string to encrypt.
   * @return encrypted string.
   */
  public static String encrypt(String strToEncrypt) {
    String encryptedStr = "";
    try {
      Key key = generateKey();
      Cipher cipher = Cipher.getInstance(ALGO);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] encryptedBytes = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
      encryptedStr = new String(Base64.getMimeEncoder().encode(encryptedBytes), StandardCharsets.UTF_8);
    } catch (Exception ex) {
      System.out.println("Cant encrypt string.");
      ex.printStackTrace();
    }
    return encryptedStr;
  }

  /**
   * Decrypts string.
   * @param strToEncrypt string to decrypt.
   * @return decrypted string.
   */
  public static String decrypt(String strToDecrypt) {
    String decryptedStr = "";
    try { 
      Key key = generateKey();
      Cipher cipher = Cipher.getInstance(ALGO);
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] decodedStrBytes = Base64.getMimeDecoder().decode(strToDecrypt.getBytes(StandardCharsets.UTF_8));
      byte[] decryptedBytes = cipher.doFinal(decodedStrBytes);
      decryptedStr = new String(decryptedBytes, StandardCharsets.UTF_8);
    } catch (Exception ex) {
      System.out.println("Cant decrypt string.");
      ex.printStackTrace();
    }
    return decryptedStr;
  }

  /**
   * Generates Key object for encrypting and decrypting data.
   * @return Key generated Key object.
   * @throws Exception if can't generate key.
   */
  private static Key generateKey() throws Exception {
   return new SecretKeySpec(KEY_STR.getBytes(), ALGO);
  }
  
}
