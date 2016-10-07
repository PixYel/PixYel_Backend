package pods_backend;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * @author JavaDigest
 *
 */
public class Encryption {

    public static void main(String[] args) throws Exception {
        //Main Class for Testing purposes ONLY
        //Generate KeyPair (PublicKey AND PrivateKey)
        KeyPair keyPair = generateKeyPair();

        //Encryptes the text with the public key
        byte[] encrypted = encrypt("Geheime Nachricht", keyPair.getPublic());

        //Decrypted the byteArray with the private key
        String decrypted = decrypt(encrypted, keyPair.getPrivate());

        System.out.println("Entschlüsselt: " + decrypted);
    }

    /**
     * Generates the Key-Pair which contains the public and the private key
     *
     * @return an Instance of KePair which contains the public and the private
     * key
     */
    public static KeyPair generateKeyPair() {
        try {
            // Get an instance of the RSA key generator
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            //Initialize it with 2048 Bit Encryption (keysize)
            kpg.initialize(2048);
            // Generate the keys — might take sometime on slow computers
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Could not create KeyPair: " + ex);
        }
        return null;
    }

    /**
     * Encryptes text with a public key
     * @param text The text as String to encrypt
     * @param publicKey The public key from the KeyPair to encrypt with
     * @return The result of the encryption as byte-array
     */
    public static byte[] encrypt(String text, PublicKey publicKey) {
        try {
            // Get an instance of the Cipher for RSA encryption/decryption
            Cipher encrypter = Cipher.getInstance("RSA");
            // Initiate the Cipher, telling it that it is going to Encrypt, giving it the public key
            encrypter.init(Cipher.ENCRYPT_MODE, publicKey);
            //Returns the encrypted byteArray of the byteArray of the string
            return encrypter.doFinal(text.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            System.err.println("Could String not encrypt: " + ex);
        }
        return new byte[]{};
    }

    /**
     * Decryptes a byte-array with a private key
     * @param toDecrypt The byte-array to decrypt
     * @param privateKey The private Key from the KeyPait to decrypt with
     * @return The result of the decryption as String
     */
    public static String decrypt(byte[] toDecrypt, PrivateKey privateKey) {
        try {
            // Get an instance of the Cipher for RSA encryption/decryption
            Cipher decrypter = Cipher.getInstance("RSA");
            // Initiate the Cipher, telling it that it is going to Decrypt, giving it the private key
            decrypter.init(Cipher.DECRYPT_MODE, privateKey);
            //returns the String of the decrypted message
            return new String(decrypter.doFinal(toDecrypt));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            System.err.println("Could not decrypt byte-array: " + ex);
        }
        return "";
    }
}
