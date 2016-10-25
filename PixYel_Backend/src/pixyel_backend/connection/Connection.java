package pixyel_backend.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

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
            } catch (IOException e) {
                if (e.toString().contains("Socket is closed")) {
                    System.err.println("Socket geschlossen... " + e);
                    loop = false;
                }
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
                System.err.println("Socket konnte nicht geschlossen werden: " + e.getMessage());
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
            System.err.println("Adresse schon vergeben, läuft schon ein Server?: " + e.getMessage());
            System.err.println("Server wird beendet, um doppelte Server sofort zu unterbinden!");
            System.exit(0);
        } catch (IOException ex) {
            System.err.println("Server konnte nicht gestartet werden: " + ex.getMessage());
        }
        System.out.println("Server erreichbar unter " + SERVER.getInetAddress() + ":" + SERVER.getLocalPort());
        onServerStarted();
        this.listenForClients();
    }

    private void onServerClosing() {

    }

    private void onServerStarted() {

    }

    private void onClientConnected(Client client) {
        System.out.println("Client connected");
    }

}
