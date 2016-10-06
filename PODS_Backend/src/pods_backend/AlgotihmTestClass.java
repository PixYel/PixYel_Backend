/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pods_backend;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 *
 * @author i01frajos445
 */
public class AlgotihmTestClass {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new AlgotihmTestClass().generateKeyPairs();
    }

    /**
     * Generates the Key paires
     * @return [public Key as String OR "ERROR" when an error occured] [private Key as String OR "" when an error occured]
     */
    public String[] generateKeyPairs() {
        String[] keys = new String[2];
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
            StringBuilder publicKeyString = new StringBuilder();
            for (int i = 0; i < publicKey.length; ++i) {
                publicKeyString.append(Integer.toHexString(0x0100 + (publicKey[i] & 0x00FF)).substring(1));
            }
            keys[0] = publicKeyString.toString();
            System.out.println("PublicKey: " + publicKeyString);

            byte[] privateKey = keyGen.genKeyPair().getPrivate().getEncoded();
            StringBuilder privateKeyString = new StringBuilder();
            for (int i = 0; i < privateKey.length; ++i) {
                privateKeyString.append(Integer.toHexString(0x0100 + (privateKey[i] & 0x00FF)).substring(1));
            }
            keys[1] = privateKeyString.toString();
            System.out.println("PrivateKey: " + privateKeyString);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Fehler, Algorithmus konnte nicht gefunden werden: " + e);
            keys[0] = "ERROR";
        }
        return keys;
    }
}
