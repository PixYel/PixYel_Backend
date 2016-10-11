/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author i01frajos445
 */
public class EncryptionTest {
    
    /**
     * Tests the Encryption.
     */
    @Test
    public void testKeyPair() {
        String [] keyPair = Encryption.generateKeyPair();
        String toEncrypt = "Constant"+System.currentTimeMillis()+System.getProperty("user.home");
        String encrypted = Encryption.encrypt(toEncrypt, keyPair[0]);
        String decrypted = Encryption.decrypt(encrypted, keyPair[1]);
        assertEquals(toEncrypt, decrypted);
    }

    
}
