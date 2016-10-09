/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pods_backend;

/**
 *
 * @author Josua Frank
 */
public class Test {
    public static void main(String[] args) throws XML.XMLException {        
        String[] keyPair = Encryption.generateKeyPair();
        System.out.println(new XML("clientpublickey").setContent(keyPair[0]).toXMLString());
    }
}
