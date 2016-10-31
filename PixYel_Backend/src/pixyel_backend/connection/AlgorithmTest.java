/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import java.util.Base64;
import pixyel_backend.connection.encryption.Encryption;
import pixyel_democlient.compression.Compression;

/**
 *
 * @author i01frajos445
 */
public class AlgorithmTest {
    public static void main(String[] args) {
        String toCompress = "äüöß";
        String compressed = Compression.compress(toCompress);
        System.out.println(Compression.decompress(compressed));
                
    }
}
