package pixyel_backend.encryption;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.util.Base64;

/**
 * @author Josua Frank
 *
 */
public class Encryption {

    /*
    */

    /**
     * Generates the Key-Pair which contains the public and the private key<p>
     * 
     * Using:<p>
     * {@code //Generate KeyPair (PublicKey AND PrivateKey)}<p>
        {@code String[] keyPair = generateKeyPair();}<p>
<p>
        {@code //Encryptes the text with the public key}<p>
        {@code String encrypted = encrypt("Geheime Nachricht", keyPair[0]);}<p>
<p>
        {@code //Decrypted the byteArray with the private key}<p>
        {@code String decrypted = decrypt(encrypted, keyPair[1]);}<p>
<p>
        {@code System.out.println("Entschlüsselt: " + decrypted);}<p>
     *<p>
     * @return A StringArray which contains the first the public and second the
     * private key
     */
    public static String[] generateKeyPair() {
        String[] result = new String[2];
        try {
            // Get an instance of the RSA key generator
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            //Initialize it with 2048 Bit Encryption (keysize)
            kpg.initialize(2048);
            // Generate the keys — might take sometime on slow computers
            KeyPair kp = kpg.generateKeyPair();
            //Gets the encoded public and private Keys as String
            result[0] = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());//PublicKey
            result[1] = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());//PrivateKey
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Could not create KeyPair: " + ex);
        }
        return result;
    }

    /**
     * Encryptes text with a public key
     *
     * @param text The text as String to encrypt
     * @param publicKey The public key from the KeyPair to encrypt with
     * @return The result of the encryption as byte-array
     */
    public static String encrypt(String text, String publicKey) {
        try {
            //Creates the Public Key from the String
            PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
            // Get an instance of the Cipher for RSA encryption/decryption
            Cipher encrypter = Cipher.getInstance("RSA");
            // Initiate the Cipher, telling it that it is going to Encrypt, giving it the public key
            encrypter.init(Cipher.ENCRYPT_MODE, pubKey);
            //Returns the encrypted byteArray of the byteArray of the string
            return Base64.getEncoder().encodeToString(encrypter.doFinal(text.getBytes()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException ex) {
            System.err.println("Could String not encrypt: " + ex);
        }
        return "";
    }

    /**
     * Decryptes a byte-array with a private key
     *
     * @param toDecrypt The byte-array to decrypt
     * @param privateKey The private Key from the KeyPait to decrypt with
     * @return The result of the decryption as String
     */
    public static String decrypt(String toDecrypt, String privateKey) {
        try {
            //Generates the Private Key from the byteArray
            PrivateKey privKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
            // Get an instance of the Cipher for RSA encryption/decryption
            Cipher decrypter = Cipher.getInstance("RSA");
            // Initiate the Cipher, telling it that it is going to Decrypt, giving it the private key
            decrypter.init(Cipher.DECRYPT_MODE, privKey);
            //returns the String of the decrypted message
            return new String(decrypter.doFinal(Base64.getDecoder().decode(toDecrypt)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException ex) {
            System.err.println("Could not decrypt byte-array: " + ex);
        }
        return "";
    }
}
