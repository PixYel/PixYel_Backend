/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_democlient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_democlient.compression.Compression;
import pixyel_democlient.encryption.Encryption;
import pixyel_democlient.xml.XML;

/**
 *
 * @author Josua Frank
 */
public class PixYel_Client {

    Socket socket;
    String ip = "localhost";
    String ServerPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxy9l8VAKfxIGN+syNncLSj+z+4TV/RDccfnoKrJEtIXcm0bUkU3Ajt9VlAHMzpyWYA5VT4Onsl5Pbe5UV6enFwPqCQUQVIJUzx8gBsnd3twzw6KbhMbbcXKstXVuXvd3h6VzH4ChA6aI2g7qwv7CoSgUw6149ReTXzzKt4eD8U0y/8Wbn9ns2RobakGNYKbHV3GB/jJB0C5uL/Vj5iMirnqh2mUThpHNZn+JG2CqHhtDrJeISDAZN8bBCYV/JEPgo7EzRajZ6hKUHSe1PbSRy6f9W/O7tyiTcgB/nq9BxxkZznc9WIltwCyClnesonP6OHlIrl0JZqRnAyEn0CeIcQIDAQAB";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new PixYel_Client();
    }

    public PixYel_Client() {
        connect();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PixYel_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] keyPair = Encryption.generateKeyPair();
        sendToServer(XML.createNewXML("echo").toXMLString());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PixYel_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        //disconnect();
    }

    public void connect() {
        while (socket == null || !socket.isConnected()) {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip, 7331), 500);
                new Thread(new ServerInputListener()).start();
                System.out.println("Erfolgreich verbunden");
            } catch (UnknownHostException e) {
                System.err.println("Unbekannter Host: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Server konnte nicht erreicht werden: " + e.getMessage());
            }
        }
    }

    public void disconnect() {
        if (socket == null) {
            System.out.println("Server unerreichbar, beende daher sofort...");
            System.exit(0);
        } else {
            try {
                socket.close();
                System.out.println("Habe mich abgemeldet und beende mich jetzt...");
                System.exit(0);
            } catch (IOException ex) {
                System.out.println("Konnte Socket nicht schliessen!");
            }

        }
    }

    public void sendToServer(String toSend) {
        try {
            String compressed = Compression.compress(toSend);
            //String encrypted = Encryption.encrypt(compressed, ServerPublicKey);
            PrintWriter raus = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            raus.println(compressed);
            raus.flush();
            System.out.println("sending successful");
        } catch (Exception e) {
            if (e.toString().contains("Socket is closed")) {
                System.err.println("Could not send String beacuase the socket is closed, closing the connection now: " + e);
                disconnect();
            } else if (e.toString().contains("socket write error")) {
                System.err.println("Could not write on Socket: " + e);
            } else {
                System.err.println("String(" + toSend + ") could not be send: " + e);
            }
        }
    }

    public class ServerInputListener implements Runnable {

        @Override
        public void run() {
            System.out.println("Inputlistener for Server " + socket.hashCode() + " started");
            BufferedReader rein;
            String string;
            while (!socket.isClosed() && socket.isConnected() && socket.isBound()) {
                try {
                    rein = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    string = rein.readLine();
                    onStringReceived(string);
                } catch (IOException exe) {
                    switch (exe.toString()) {
                        case "java.net.SocketException: Connection reset":
                        case "java.net.SocketException: Socket closed":
                            //System.err.println("Client has lost Connection: " + exe + ", shuting down the connection to the client");
                            disconnect();
                            break;
                        case "invalid stream header":
                            //Jemand sendet zu lange Strings
                            System.err.println("Steam header too long, received String too long??!?: " + exe);
                            disconnect();
                            break;
                        default:
                            System.err.println("Could not read incomming message: " + exe);
                            break;
                    }
                }
            }
        }
    }

    public void onStringReceived(String string) {
        String decompressed = Compression.decompress(string);
        try {
            System.out.println(XML.openXML(decompressed));
        } catch (Exception e) {
        }
    }

}
