package pods_backend;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author JavaDigest
 *
 */
public class Encryption {

    public static void main(String[] args) throws Exception {
        //Main Class for Testing purposes ONLY
        KeyPair keyPair = generateKeyPair();

        byte[] encrypted = encrypt("Geheime Nachricht", keyPair.getPublic());
        String decrypted = decrypt(encrypted, keyPair.getPrivate());

        System.out.println("Entschlüsselt: " + decrypted);
    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        // Get an instance of the RSA key generator
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        //Initialize it with 2048 Bit Encryption (keysize)
        kpg.initialize(2048);
        // Generate the keys — might take sometime on slow computers
        return kpg.generateKeyPair();
    }

    public static byte[] encrypt(String text, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        // Get an instance of the Cipher for RSA encryption/decryption
        Cipher encrypter = Cipher.getInstance("RSA");
        // Initiate the Cipher, telling it that it is going to Encrypt, giving it the public key
        encrypter.init(Cipher.ENCRYPT_MODE, publicKey);
        
        return encrypter.doFinal(text.getBytes());
    }

    public static String decrypt(byte[] toDecrypt, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException {
        // Get an instance of the Cipher for RSA encryption/decryption
        Cipher decrypter = Cipher.getInstance("RSA");
        // Initiate the Cipher, telling it that it is going to Decrypt, giving it the private key
        decrypter.init(Cipher.DECRYPT_MODE, privateKey);

        return new String(decrypter.doFinal(toDecrypt));
    }
}
