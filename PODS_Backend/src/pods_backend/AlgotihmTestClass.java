/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pods_backend;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author i01frajos445
 */
public class AlgotihmTestClass {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        AlgotihmTestClass test = new AlgotihmTestClass();
        Key[] keys = AlgotihmTestClass.generateKeyPairs();

        String messageToEncrypt = "Hd!";
        System.out.println("Plain: " + messageToEncrypt);
        byte[] encrypted = AlgotihmTestClass.encrypt(messageToEncrypt, (PublicKey) keys[0]);
        System.out.println("Encyrpted: " + Arrays.toString(encrypted));
        String decrypted = AlgotihmTestClass.decrypt(encrypted, (PrivateKey) keys[1]);
        System.out.println("Decrypted: " + decrypted);
    }

    /**
     * Generates the Key paires
     *
     * @return [public Key as Key OR NULL when an error occured] [private Key as
     * Key OR NULL when an error occured]
     */
    public static Key[] generateKeyPairs() {
        Key[] keys = new Key[2];
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);

            keys[0] = keyGen.genKeyPair().getPublic();
            keys[1] = keyGen.genKeyPair().getPrivate();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Fehler, Algorithmus konnte nicht gefunden werden: " + e);
        }
        return keys;
    }

  public static byte[] encrypt(String text, PublicKey key) {
    byte[] cipherText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance("RSA");
      // encrypt the plain text using the public key
      cipher.init(Cipher.ENCRYPT_MODE, key);
      cipherText = cipher.doFinal(text.getBytes());
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
    }
    return cipherText;
  }

  /**
   * Decrypt text using private key.
   * 
     * @param toDecrypt
   * @param key
   *          :The private key
   * @return plain text
   */
  public static String decrypt(byte[] toDecrypt, PrivateKey key) {
    byte[] dectyptedText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance("RSA");

      // decrypt the text using the private key
      cipher.init(Cipher.DECRYPT_MODE, key);
      dectyptedText = cipher.doFinal(toDecrypt);

    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
        System.err.println("Fehler beim Entschlüsseln: "+ex);
    }

    return new String(dectyptedText);
  }
}
