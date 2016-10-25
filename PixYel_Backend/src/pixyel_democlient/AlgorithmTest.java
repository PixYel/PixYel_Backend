/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_democlient;

import pixyel_democlient.compression.Compression;
import pixyel_democlient.encryption.Encryption;

/**
 *
 * @author i01frajos445
 */
public class AlgorithmTest {

    static String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmG8OfhJrkN9/rXLh7auyUPcq7UxmYModYswChY8hIMgZO4m+cxOWopxOptUAYedjA4ZAKGp/P1g6n6YaXvtPQqIbi7G5oCT4vbh0zYFgI3wNCJlKtUX1gb6uCQW3rPinANcPtlZoIyegAsn/OW0FMZtc1x8PN0H1MQTlcCctXdJdotuljeYriO1lkRfb3GsotLIYjciMqIMKGQRQ2Rhj81bnxP9FybdNuVIjlS6Rfx9fzaZ2BKIdm7O7/Dzn9TcSZEOZdOSS7CHMMKr14O26g+bR2HiGWx8AbOH2zP3DMpR9/Y8GUrjO6QPqA+GorICGYWxIlrcm4iYx8740FsDaQQIDAQAB";

    public static void main(String[] args) {
        String test = "<echo/>";
        System.out.println("Original: " + test);
        String testKomprimiert = Compression.compress(test);
        System.out.println("Komprimiert: " + testKomprimiert);
        String testVerschlüsselt = Encryption.encrypt(testKomprimiert, key);
        System.out.println("Verschlüsselt: " + testVerschlüsselt);
    }

}
