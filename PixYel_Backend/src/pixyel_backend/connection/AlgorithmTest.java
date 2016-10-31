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
        String pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtwvKYSo1pCjoy8uTSOvCdkOWApxyFjbWvM9orE7tJQ2S3P0or4/3GnT/XQPXVsstPEVkhZDrqu0LKZR9vkGAAOzFB0qQ+JISMqelHntyDNt4f5AhK7hCeuTNMPRAkfLMoZVhwjrjHswgRw5vB3JeGMvtK25u6UhbwoMDnpaGWs5pv493ZbLHy4qp/AX57SYDBE1xDdeaZxMfkKWATf/fIrP1CK5HrEFjIIVMbOplunzkUDUWE/NX+u3AXYDDsM0ahV2WaC09ALS9b+7/v7mhkT8U9/7dllElaVL/5awGSYGVP7eSGdNukgjfFnJLq3d/qdpxG7jtoQHtQ0xg3fOMRQIDAQAB";
        String demo = "HALLO";
        System.out.println(Encryption.encrypt(demo, pubKey));
    }
}
