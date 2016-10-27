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
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.Log;
import pixyel_backend.connection.compression.Compression;
import pixyel_backend.connection.encryption.Encryption;
import pixyel_backend.database.objects.User;
import pixyel_backend.xml.XML;

/**
 *
 * @author i01frajos445
 */
public class Client implements Runnable {

    private final Socket socket;
    private static final String SERVERPRIVATEKEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCYbw5+EmuQ33+tcuHtq7JQ9yrtTGZgyh1izAKFjyEgyBk7ib5zE5ainE6m1QBh52MDhkAoan8/WDqfphpe+09CohuLsbmgJPi9uHTNgWAjfA0ImUq1RfWBvq4JBbes+KcA1w+2VmgjJ6ACyf85bQUxm1zXHw83QfUxBOVwJy1d0l2i26WN5iuI7WWRF9vcayi0shiNyIyogwoZBFDZGGPzVufE/0XJt025UiOVLpF/H1/NpnYEoh2bs7v8POf1NxJkQ5l05JLsIcwwqvXg7bqD5tHYeIZbHwBs4fbM/cMylH39jwZSuM7pA+oD4aisgIZhbEiWtybiJjHzvjQWwNpBAgMBAAECggEAa9DRPVamAlgypGnHZyW0ABRyplkNaJRMaJ7HgdQUqG0fe78Xl6lZODa6YsHxfU39+HYyVuPMkO9Et7rymA/Epeqm9Q2Fr8G2teoLo3dImpCZX2WdM84Bsf0+d1815QASj0ON93fyPDtAr/hTrzhvHgE3j2iGiJz8YemGpubu7ZZPVy89hwNhFcLZxEj8ZTI07nuy+uSiOaJQhrIl8jqH/Y099ex838zP4Bkqo321q5Rb0eYKB+cAB2oU6GQ411mARzkOwB6TI+PHB4l6MPryR24fO0JokiRafs178K3sRIEG2ZwC6VS/5cYDzoksEC/ztmmVV8UBmZXgTJqlQ9OjYQKBgQDTKXQw4pg7V9dV1ku+VrMc09EkI76SX+gZ8+qu4ogM4BFx7ifuwORRAv8pvvKXLN1N0o1br4JgEHnspAZdBOi/GqSnk93Mzdyu4eVeNJsWCW9uQG+x3JutsI4uTipGL3Ujf3RNVVBLz8Mn09iWKBq9x+k3DO6NS7ZsYIK7ueDyBQKBgQC4zTPeoGicyAiONGRwatS94gB7O0OB1OmR0ogjgHxKnZYji1fZZB3MtvspIThFbFM899+iRNwCm9DBSswzmAHCrUEdMVSapuLIcDnFFBD4bsGIi1WQSR0/sq9KXeDb7y2mXDOBdQKT+/YBYCIFSc0uMYuTKPJETXZbAZIqn1dQDQKBgFDmDNG9vjzeBm2oUBR3+t8Md4+08gn6HF31CPA/cYgdQBG6ACqEU/SFhWRk92PbmF18URPTRcuwBEYZAScZ/mFYv8RD7jHMnMOTX+Cbnt7udnZy0Vf/ANWmUpKC98cz31jeN5x5Fu40hrv3eX7tlnEm6b5hgfM4Eoeq4esx53BRAoGAfowWbucNe/8GzMpX1Rty3yx6A+kLS0bPQwoWK6l6c2YCQAjVeYozVdIfn4SeEfwh6+gZffcFacvlYdekidgXrIYTxrfXJagOOA+Rn8Ej9dtyL9yqFQncO5hSrOwoZLMeYYNVydqkj03EoiCAa3qkRHOtDnLFo7wfxiYHP6Spl6kCgYAyE7LN0YZFqHqsvYhLcJVfQtW2kEJp/1HnXIwMHLqDJVeQf3+JPGXZ9vAaonz1bKL9LNdkPXGjVYwxJ99HEdahwgD9TGs57MjfliF4tG9h38KCpSiGD1PXRq3/GJt2XsG5xK5gglBOauAVWRDZAZNqDCNiTIQtw3eXatyZPklrSw==";
    protected User userdata;

    public Client(Socket socket) {
        this.socket = socket;
    }

    ClientInputListener listener;

    @Override
    public void run() {
        Log.logInfo("Client " + socket.hashCode() + " started", this);
        listener = new ClientInputListener();
        new Thread(listener).start();
    }

    /**
     * Sends a String to the Client
     *
     * @param toSend The String to be send, not allowed to be
     */
    public void sendToClient(String toSend) {
        if (userdata == null || userdata.getPublicKey() == null || userdata.getPublicKey().isEmpty()) {
            Log.logError("Log in first!", this);
            return;
        }
        try {
            String compressed = Compression.compress(toSend);
            String encrypted = Encryption.encrypt(compressed, userdata.getPublicKey());
            PrintWriter raus = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            raus.println(encrypted);
            raus.flush();
        } catch (Exception e) {
            if (e.toString().contains("Socket is closed")) {
                Log.logError("Could not send String beacuase the socket is closed, closing the connection now: " + e, this);
                this.disconnect(false);
            } else if (e.toString().contains("socket write error")) {
                Log.logError("Could not write on Socket: " + e, this);
            } else {
                Log.logError("String(" + toSend + ") could not be send: " + e, this);
            }
        }
    }

    /**
     * This method is going to be called as soon as a string was send to this
     * client
     *
     * @param receivedString The received encrypted and compressed string
     */
    private void onStringReceived(String receivedString) {
        //------------TEMP-START-------------
        if (receivedString.equals("echo")) {
            PrintWriter raus;
            try {
                raus = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                raus.println("echo zurueck " + new java.util.Date().toString());
                raus.flush();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        //--------------TEMP-END-------------
        try {
            System.out.println("Received String: " + receivedString);
            String decrypted = Encryption.decrypt(receivedString, SERVERPRIVATEKEY);
            String decompressed = Compression.decompress(decrypted);
            XML xml = XML.openXML(decompressed);
            Command.onCommandReceived(this, xml);
        } catch (XML.XMLException ex) {
            Log.logError("Client has send an invalid XML: " + ex, this);
        }
    }

    /**
     * Call this method to disconnect the client from this server
     *
     * @param expected Is the disconnect expected?
     */
    public void disconnect(boolean expected) {
        if (userdata != null) {
            userdata.closeDbConnection();
        }
        Connection.disconnect(socket.hashCode());
        try {
            socket.close();
        } catch (Exception e) {
            Log.logError("Could not close the clients socket", this);
        }
    }

    /**
     * Returns the name (Store ID) of this client or the hash code of the
     * socket, if its not yet logged in
     *
     * @return The name (Store ID) of this client or the hash code of the
     * socket, if its not yet logged in
     */
    public String getName() {
        if (userdata != null) {
            return userdata.getStoreID();
        } else {
            return String.valueOf(socket.hashCode());
        }
    }

    /**
     * The Class, which looks for Strings from the client
     */
    public class ClientInputListener implements Runnable {

        @Override
        public void run() {
            Log.logInfo("Inputlistener for Client " + socket.hashCode() + " started", this);
            BufferedReader rein;
            String string;
            while (!socket.isClosed() && socket.isConnected() && socket.isBound() && !socket.isInputShutdown() && !socket.isOutputShutdown()) {
                try {
                    rein = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    string = rein.readLine();
                    if (string != null) {
                        onStringReceived(string);
                    }
                } catch (IOException exe) {
                    switch (exe.toString()) {
                        case "java.net.SocketException: Connection reset":
                        case "java.net.SocketException: Socket closed":
                        case "java.net.SocketException: Software caused connection abort: recv failed":
                            Log.logError("Client has lost Connection: " + exe + ", shuting down the connection to the client", this);
                            disconnect(true);
                            return;
                        case "invalid stream header":
                            //Jemand sendet zu lange Strings
                            Log.logError("Steam header too long, received String too long??!?: " + exe, this);
                            disconnect(true);
                            return;
                        default:
                            Log.logError("Could not read incomming message: " + exe, this);
                            break;
                    }
                }
            }
        }

    }

}
