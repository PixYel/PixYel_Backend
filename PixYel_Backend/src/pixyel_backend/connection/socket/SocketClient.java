/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import pixyel_backend.Log;
import pixyel_backend.connection.Client;
import pixyel_backend.connection.Command;
import pixyel_backend.connection.encryption.Encryption;
import pixyel_backend.database.objects.User;
import pixyel_backend.xml.XML;

/**
 *
 * @author Josua Frank
 */
public class SocketClient implements Runnable, Client {

    private final Socket socket;
    private static final String SERVERPRIVATEKEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCYbw5+EmuQ33+tcuHtq7JQ9yrtTGZgyh1izAKFjyEgyBk7ib5zE5ainE6m1QBh52MDhkAoan8/WDqfphpe+09CohuLsbmgJPi9uHTNgWAjfA0ImUq1RfWBvq4JBbes+KcA1w+2VmgjJ6ACyf85bQUxm1zXHw83QfUxBOVwJy1d0l2i26WN5iuI7WWRF9vcayi0shiNyIyogwoZBFDZGGPzVufE/0XJt025UiOVLpF/H1/NpnYEoh2bs7v8POf1NxJkQ5l05JLsIcwwqvXg7bqD5tHYeIZbHwBs4fbM/cMylH39jwZSuM7pA+oD4aisgIZhbEiWtybiJjHzvjQWwNpBAgMBAAECggEAa9DRPVamAlgypGnHZyW0ABRyplkNaJRMaJ7HgdQUqG0fe78Xl6lZODa6YsHxfU39+HYyVuPMkO9Et7rymA/Epeqm9Q2Fr8G2teoLo3dImpCZX2WdM84Bsf0+d1815QASj0ON93fyPDtAr/hTrzhvHgE3j2iGiJz8YemGpubu7ZZPVy89hwNhFcLZxEj8ZTI07nuy+uSiOaJQhrIl8jqH/Y099ex838zP4Bkqo321q5Rb0eYKB+cAB2oU6GQ411mARzkOwB6TI+PHB4l6MPryR24fO0JokiRafs178K3sRIEG2ZwC6VS/5cYDzoksEC/ztmmVV8UBmZXgTJqlQ9OjYQKBgQDTKXQw4pg7V9dV1ku+VrMc09EkI76SX+gZ8+qu4ogM4BFx7ifuwORRAv8pvvKXLN1N0o1br4JgEHnspAZdBOi/GqSnk93Mzdyu4eVeNJsWCW9uQG+x3JutsI4uTipGL3Ujf3RNVVBLz8Mn09iWKBq9x+k3DO6NS7ZsYIK7ueDyBQKBgQC4zTPeoGicyAiONGRwatS94gB7O0OB1OmR0ogjgHxKnZYji1fZZB3MtvspIThFbFM899+iRNwCm9DBSswzmAHCrUEdMVSapuLIcDnFFBD4bsGIi1WQSR0/sq9KXeDb7y2mXDOBdQKT+/YBYCIFSc0uMYuTKPJETXZbAZIqn1dQDQKBgFDmDNG9vjzeBm2oUBR3+t8Md4+08gn6HF31CPA/cYgdQBG6ACqEU/SFhWRk92PbmF18URPTRcuwBEYZAScZ/mFYv8RD7jHMnMOTX+Cbnt7udnZy0Vf/ANWmUpKC98cz31jeN5x5Fu40hrv3eX7tlnEm6b5hgfM4Eoeq4esx53BRAoGAfowWbucNe/8GzMpX1Rty3yx6A+kLS0bPQwoWK6l6c2YCQAjVeYozVdIfn4SeEfwh6+gZffcFacvlYdekidgXrIYTxrfXJagOOA+Rn8Ej9dtyL9yqFQncO5hSrOwoZLMeYYNVydqkj03EoiCAa3qkRHOtDnLFo7wfxiYHP6Spl6kCgYAyE7LN0YZFqHqsvYhLcJVfQtW2kEJp/1HnXIwMHLqDJVeQf3+JPGXZ9vAaonz1bKL9LNdkPXGjVYwxJ99HEdahwgD9TGs57MjfliF4tG9h38KCpSiGD1PXRq3/GJt2XsG5xK5gglBOauAVWRDZAZNqDCNiTIQtw3eXatyZPklrSw==";
    private User userdata;
    private final ExecutorService listener;
    private final Timer clientAliveTimer;

    public SocketClient(Socket socket) {
        this.socket = socket;
        listener = Executors.newSingleThreadExecutor();
        clientAliveTimer = new Timer();
    }

    @Override
    public void run() {
        Log.logDebug("Client " + getName() + " started", SocketClient.class);
        startInputListener();
        startClientAliveTimer();
    }

    PrintWriter raus;

    private void sendToClient(String toSend) {
        try {
            if (raus == null) {
                raus = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            }
            raus.println(toSend);
            raus.flush();
            Log.logDebug("Successfullly send unencrypted XML", SocketClient.class);
        } catch (IOException e) {
            if (e.toString().contains("Socket is closed")) {
                Log.logWarning("Could not send String beacuase the socket is closed, closing the connection to " + getName() + " now: " + e, SocketClient.class);
                this.disconnect(false);
            } else if (e.toString().contains("socket write error")) {
                Log.logWarning("Could not write on Socket from " + getName() + ": " + e, SocketClient.class);
            } else {
                Log.logWarning("String(" + toSend + ") could not be send to " + getName() + ": " + e, SocketClient.class);
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
        if (receivedString.endsWith("\\n")) {
            receivedString = receivedString.substring(0, receivedString.length() - 2);
        }
        try {
            if (!receivedString.startsWith("<request>")) {
                Log.logDebug("ENCRYPTED_RECEIVED: " + receivedString, SocketClient.class);
                String decrypted = Encryption.decrypt(receivedString, SERVERPRIVATEKEY);
                //Log.logDebug("DECRYPTED_RECEIVED: " + decrypted, SocketClient.class);
                //String decompressed = Compression.decompress(decrypted);
                Log.logDebug("PLAIN_RECEIVED: " + decrypted, SocketClient.class);
                XML xml = XML.openXML(decrypted);
                lastCommandReceivedOn = System.currentTimeMillis();
                sendToClient(Command.onCommandReceived(this, xml, true));
            } else {
                Log.logDebug("PLAIN_RECEIVED: " + receivedString, SocketClient.class);
                XML xml = XML.openXML(receivedString);
                lastCommandReceivedOn = System.currentTimeMillis();
                Command.onCommandReceived(this, xml, false);
            }
        } catch (XML.XMLException ex) {
            Log.logWarning("Client " + getName() + " has send an invalid String to parse as XML: " + ex, SocketClient.class);
        } catch (Encryption.EncryptionException ex) {
            Log.logWarning("Client " + getName() + " has send an invalid String to decrypt: " + ex, SocketClient.class);
        } catch (Exception ex) {
            Log.logWarning("Client " + getName() + " has send an invalid String: " + ex, SocketClient.class);
        }
    }

    @Override
    public void disconnect() {
        disconnect(true);
    }

    /**
     * Call this method to disconnect the client from this server
     *
     * @param expected Is the disconnect expected?
     */
    public void disconnect(boolean expected) {
        lookingForInput = false; //interrupts the while(true) on the inputlistener
        listener.shutdown(); //shuts the listener thread down
        clientAliveTimer.cancel(); //Shuts the timer for the client live down
        SocketServer.disconnect(this, socket.hashCode()); //Runs the onClientClosed method for futher instructions and removes this client from the loggedInClientsmap (which is used for checking of double logged in clients)
        try {
            socket.close();
        } catch (Exception e) {
            Log.logError("Could not close the socket of the client " + getName(), SocketClient.class);
        }
    }

    /**
     * Returns the name (Store ID) of this client or the hash code of the
     * socket, if its not yet logged in
     *
     * @return The name (Store ID) of this client or the hash code of the
     * socket, if its not yet logged in
     */
    @Override
    public String getName() {
        if (userdata != null) {
            return userdata.getStoreID();
        } else {
            return "[" + String.valueOf(socket.getInetAddress()) + "]";
        }
    }

    private long lastCommandReceivedOn = System.currentTimeMillis();
    private int clientTimeOutInSeconds = 60;

    /**
     * Starts the Timer for the SocketClient
     */
    public void startClientAliveTimer() {
        clientAliveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if ((lastCommandReceivedOn + (clientTimeOutInSeconds * 1000)) < System.currentTimeMillis()) {
                    Log.logInfo("Client " + getName() + " was too long inactive! Disconnecting...", SocketClient.class);
                    disconnect(false);
                }
            }
        }, 0, clientTimeOutInSeconds * 1000);
    }

    boolean lookingForInput = true;

    /**
     * Starts tht InputListener for the Input from the client
     */
    public void startInputListener() {
        listener.submit(() -> {
            //Log.logInfo("Inputlistener for SocketClient " + getName() + " started", SocketClient.class);
            BufferedReader rein;
            try {
                rein = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException ex) {
                Log.logError("Could not create InputStream for the client " + getName() + ": " + ex, SocketClient.class);
                return;
            }
            String string;
            while (lookingForInput && !socket.isClosed() && socket.isConnected() && socket.isBound() && !socket.isInputShutdown() && !socket.isOutputShutdown()) {
                try {

                    string = rein.readLine();
                    if (string.startsWith("<EOF>")) {
                        string = string.substring(5);
                    }
                    if (string.endsWith("<EOF>")) {
                        string = string.substring(0, string.length() - 5);
                    }
                    if (string != null) {
                        onStringReceived(string);
                    } else {
                        Log.logWarning("Client " + getName() + " is sending NULL Strings", SocketClient.class);
                    }
                } catch (IOException exe) {
                    if (lookingForInput) {
                        switch (exe.toString()) {
                            case "java.net.SocketException: Connection reset":
                            case "java.net.SocketException: Socket closed":
                            case "java.net.SocketException: Software caused connection abort: recv failed":
                                Log.logWarning("Client " + getName() + " has lost Connection: " + exe + ", shuting down the connection to the client", SocketClient.class);
                                disconnect(true);
                                return;
                            case "invalid stream header":
                                //Jemand sendet zu lange Strings
                                Log.logError("Steam header too long, received String from " + getName() + " too long??!?: " + exe, SocketClient.class);
                                disconnect(true);
                                return;
                            default:
                                Log.logError("Could not read incomming message from " + getName() + ": " + exe, SocketClient.class);
                                break;
                        }
                    }
                }
            }
        });
    }

    /**
     * Sets the reference for the database for this user
     *
     * @param user
     */
    public void setUserdata(User user) {
        if (user == null) {
            Log.logDebug("No User to be set", SocketClient.class);
        }
        this.userdata = user;
        SocketServer.removePossibleDoubleClients(this);
    }

    /**
     * Returns the data from the database of this user
     *
     * @return The data from the database of this user
     */
    @Override
    public User getUserdata() {
        return this.userdata;
    }

}
