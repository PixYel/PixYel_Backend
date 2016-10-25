package pixyel_backend.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import pixyel_backend.Log;

/**
 *
 * @author Josua Frank
 */
public class Connection implements Runnable {

    private static ServerSocket SERVER = null;
    private static HashMap<Integer, Client> CONNECTEDCLIENTS = new HashMap<>();//Nur die eingeloggten Clienten (Geht somit schneller zum getUsername

    /**
     * Hier wird der Server gestartet.
     *
     */
    public static void start() {
        new Thread(new Connection()).start();
    }

    /**
     * Sucht unendlich lang nach Clients, die sich verbinden möchten. Sobald
     * sich ein Client meldet, weist er ihm einen eigenen Thread (Client) zu.
     */
    private void listenForClients() {
        boolean loop = true;
        Socket socket;
        while (loop) {
            try {
                socket = SERVER.accept();
                Client client = new Client(socket);
                CONNECTEDCLIENTS.put(socket.hashCode(), client);
                new Thread(client).start();
                this.onClientConnected(client);
            } catch (Exception e) {
                Log.logError("IO Error occured during the setup of the connection to the client: " + e);
                loop = false;
            }
        }
    }

    /**
     * Muss bei Disconnect des Servers aufgerufen werden
     *
     */
    public void stopServer() {
        if (SERVER != null) {
            try {
                onServerClosing();
                SERVER.close();
                System.exit(0);
            } catch (IOException e) {
                Log.logError("Socket could not be closed: " + e.getMessage());
            }
        }
    }

    /**
     * Sendet ein Objekt zu allen verbundenen Clienten
     *
     * @param stringToSend
     */
    public static void sendToEveryone(String stringToSend) {
        CONNECTEDCLIENTS.entrySet().stream().forEach((clientIdPair) -> {
            clientIdPair.getValue().sendToClient(stringToSend);
        });
    }

    /**
     * Gibt den ClientManager des jeweiigen Benutzers zurück
     *
     * @param id Der Benutzer, dessen Client gesucht wird
     * @return Der ClientManager des Benutzers
     */
    public static Client getClient(int id) {
        return CONNECTEDCLIENTS.get(id);
    }

    /**
     * Entfernt einen Clienten von der Clientmanagerliste, z.B. falls er seine
     * Verbindung trennt
     *
     * @param client Der zu entfernende ClientManager
     */
    public static void removeFromClientList(Client client) {
        try {
            CONNECTEDCLIENTS.remove(client.userdata.getID());
            CONNECTEDCLIENTS.remove(client.socket.hashCode());//In the case he hasnt commited his device id and telephone number   
        } catch (Exception e) {
        }
    }

    public static void addIDToMap(Client client) {
        CONNECTEDCLIENTS.remove(client.socket.hashCode());
        CONNECTEDCLIENTS.put(client.userdata.getID(), client);
    }

    @Override
    public void run() {
        try {
            SERVER = new ServerSocket(7331);
        } catch (java.net.BindException e) {
            Log.logError("Adress already binded, is there an existing server running?: " + e.getMessage());
            Log.logError("Shutting down this server to prevent double servers!");
            System.exit(0);
        } catch (IOException ex) {
            Log.logError("Server could not be started: " + ex.getMessage());
        }
        onServerStarted();
        this.listenForClients();
    }

    /**
     * Calls this Method just before its closing its socket
     */
    private void onServerClosing() {
        Log.logInfo("Shutting down the Server...");
    }

    /**
     * Calls this Method right after it initialized iteself but before its
     * accepting new clients
     */
    private void onServerStarted() {
        Log.logInfo("Server reachable on " + SERVER.getLocalSocketAddress() + ":" + SERVER.getLocalPort());
    }

    /**
     * Calls this method right after the socket connection from a new client is
     * established, the client runs in a seperate thread
     *
     * @param client
     */
    private void onClientConnected(Client client) {
        Log.logInfo("Client connected");
    }

}
