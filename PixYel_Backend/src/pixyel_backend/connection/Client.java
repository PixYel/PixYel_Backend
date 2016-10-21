/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import pixyel_backend.connection.compression.Compression;
import pixyel_backend.database.objects.User;
import pixyel_backend.xml.XML;

/**
 *
 * @author i01frajos445
 */
public class Client implements Runnable {

    Socket socket;
    private static final String SERVERPRIVATEKEY = "MIIEvABADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDHL2XxUAp/EgY36zI2dwtKP7P7hNX9ENxx+egqskS0hdybRtSRTcCO31WUAczOnJZgDlVPg6eyXk9t7lRXp6cXA+oJBRBUglTPHyAGyd3e3DPDopuExttxcqy1dW5e93eHpXMfgKEDpojaDurC/sKhKBTDrXj1F5NfPMq3h4PxTTL/xZuf2ezZGhtqQY1gpsdXcYH+MkHQLm4v9WPmIyKueqHaZROGkc1mf4kbYKoeG0Osl4hIMBk3xsEJhX8kQ+CjsTNFqNnqEpQdJ7U9tJHLp/1b87u3KJNyAH+er0HHGRnOdz1YiW3ALIKWd6yic/o4eUiuXQlmpGcDISfQJ4hxAgMBAAECggEAXCOE2LfZ4yYHUl6t/5vZ0SLesv6jrkjacK7VwspsFcXGYt3E/uIkrPKKt8iNYRX2ScDXncydClmuDIAu681V8cORw1JgV3dim90eB4Xh6HGcUGcr4aPLZxNQ6S/FOpDm7gyO0IfD89hZfeVgm4sVpU7RLCNWzKl3Npr8eyFo23m1d7JZDG9aoU0rYzmNCqlGiefaPsgFESQOv9nwEl8x7ElSziPHb3e9F+PGe4uWg4VSlCPpsWHx3kDs2vDgXOJ2TrqMctkNcsbBC//2kqKYdowBQHYmdeZJpb1llCsG96y4JQt0zoJfXJSiMewOPcGdUrRkGnGJhIRVAZy7BA5k8QKBgQDsWmxXT39WM/QktLiSqRC5TnHlHc3x4sHWo+nkrH4S1x4wi2/v7Sa1DJ2z2nadDcnzoIpTgRZNXdMDNj/GDSa2rQeAzUFSJR379r0iSkUXObbKAFw9yXo0UDHQu7mY/mquemGaAiQToRbECoH6oDKDoIRySz9mEL/W3Mt72MNCCwKBgQDXvgqpA9glwUlj+eSU8ZAIRB9uEohJXWMinCanntk2QC3e/Re/a+Sn4AFlHixoEWZfRLWSXUdeV8idd6/S+5hbgU/9rl5F1kgYCthlmXnbKgncI+XMH38niLOqt/1M4tyPlzQw6pW9TRcJyBCUtVhwlBdeQBMZiiGNbl4TuW6I8wKBgHferE/ju7smgk4TcgC/ygGRbWY8JXTmAlUvsLIPYamAfIk2TsEqT5VHHrxWsg2ZLrFm3IDZWUxHp3tbiiCSDu5a9xE2CEj3FivxyZ6YeE39MV1lrWKQym+5tGoeq1SSw9xVqSaIq29hJcCra5yGntfvEv08eJz1JbZ9KJ5ZIz5dAoGAfXb+uhYLQMGsjaDJJzloWvZLFPdgHNJbkcgfzsFZbuxaAOFNSzxSyLJD/WtTxd3AnEzYD1uB4sLIX5M4REme7DBCtbQPDSw11w2PlV5w+Fz1SPnzbQcwKmycl/7SJ0GTpUn4xy3VkTZg3IZl5iWkXiwxftVihirHonl56pyfRLMCgYAORrIGgPqzEGpHxGYkYK+xks6B5AoQJKwV7FQv/knRYciRB6Irg22778EorWUS+PPzjHwqBHTdk96NpAuU6Qa3a4w/Y5oXpD7l7u0jBYWRRt9fsVQUp/ULHS5Tnj0OdQATwG4T6uuRnKAVBb7kRC50GQXah96/o10SMEG//QdJWA==";
    User clientData;

    public Client(Socket socket) {
        this.socket = socket;

    }

    ClientInputListener listener;

    @Override
    public void run() {
        System.out.println("Client " + socket.hashCode() + " started");
        //inputListenerThread = Executors.newFixedThreadPool(1);
        listener = new ClientInputListener();
        new Thread(listener).start();
    }

    public void sendToClient(String toSend) {
        try {
            String compressed = Compression.compress(toSend);
            //String encrypted = Encryption.encrypt(compressed, clientData.getPublicKey());
            PrintWriter raus = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            raus.println(compressed);
            raus.flush();
        } catch (Exception e) {
            if (e.toString().contains("Socket is closed")) {
                System.err.println("Could not send String beacuase the socket is closed, closing the connection now: " + e);
                this.disconnect(false);
            } else if (e.toString().contains("socket write error")) {
                System.err.println("Could not write on Socket: " + e);
            } else {
                System.err.println("String(" + toSend + ") could not be send: " + e);
            }
        }
    }

    public void onStringReceived(String receivedString) {
        try {
            System.out.println("String received: " + receivedString);
            //String decrypted = Encryption.decrypt(receivedString, SERVERPRIVATEKEY);
            String decompressed = Compression.decompress(receivedString);
            XML xml = XML.openXML(decompressed);
            System.out.println("String received: " + xml.toString());
            onCommandReceived(xml);
        } catch (XML.XMLException ex) {
            System.err.println("Client has send an invalid XML: " + ex);
        }
    }

    public void disconnect(boolean expected) {
        Connection.removeFromClientList(this);
        try {
            socket.close();
        } catch (Exception e) {
            System.err.println("Could not stop ");
        }
        System.out.println("Client stopped");
    }

    public class ClientInputListener implements Runnable {

        @Override
        public void run() {
            System.out.println("Inputlistener for Client " + socket.hashCode() + " started");
            BufferedReader rein;
            String string;
            while (!socket.isClosed() && socket.isConnected() && socket.isBound() && !socket.isInputShutdown() && !socket.isOutputShutdown()) {
                try {
                    rein = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    string = rein.readLine();
                    onStringReceived(string);
                } catch (IOException exe) {
                    switch (exe.toString()) {
                        case "java.net.SocketException: Connection reset":
                        case "java.net.SocketException: Socket closed":
                            System.err.println("Client has lost Connection: " + exe + ", shuting down the connection to the client");
                            disconnect(true);
                            return;
                        case "invalid stream header":
                            //Jemand sendet zu lange Strings
                            System.err.println("Steam header too long, received String too long??!?: " + exe);
                            disconnect(true);
                            return;
                        default:
                            System.err.println("Could not read incomming message: " + exe);
                            break;
                    }
                }
            }
            System.err.println("Out of the endless while!");
        }

    }

    public void onCommandReceived(XML xml) {
        try {
            System.out.println(xml);
            switch (xml.getName()) {
                case "login":
//                    int telephonenumber = Integer.valueOf(xml.getFirstChild("telephonenumber").getContent());
//                    String deviceID = xml.getFirstChild("deviceid").getContent();
//                    if ((clientData = User.getUser(telephonenumber, deviceID)) != null) {//Existing user
//                        Connection.addIDToMap(this);
//                    } else {//New User
//                        Connection.addIDToMap(this);
//                        User.addNewUser(telephonenumber, deviceID);
//                        clientData = User.getUser(telephonenumber, deviceID);
//                        clientData.setPublicKey(xml.getFirstChild("publickey").getContent());
//                    }

                    break;
                case "smscode":

                    break;
                case "echo":
                    sendToClient("echo");
                    break;

            }
        } catch (Exception e) {
            System.err.println("Could not execute command: " + xml.getName() + ": " + e);
        }

    }

}
