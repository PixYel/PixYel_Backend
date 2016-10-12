package pixyel_backend.connection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;

/**
 *
 * @author Josua Frank
 */
public class Connection implements Runnable {

    private static ServerSocket SERVER = null;
    private static ExecutorService THREADPOOL;
    private final HashMap<String, Client> clientManagerMap = new HashMap<>();//Nur die eingeloggten Clienten (Geht somit schneller zum getUsername

    /**
     * Hier wird der Server gestartet.
     *
     */
    public static void start() {
        Executors.newFixedThreadPool(1).submit(new Connection());
    }

    /**
     * Sucht unendlich lang nach Clients, die sich verbinden möchten. Sobald
     * sich ein Client meldet, weist er ihm einen eigenen Thread (Client)
     * zu.
     */
    private void listenForClients() {
        boolean loop = true;
        Socket socket;
        while (loop) {
            try {
                socket = SERVER.accept();
                Client client = new Client(socket);
                THREADPOOL.submit(client);
                this.clientManagerMap.put(String.valueOf(socket.hashCode()), client);
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
     * @param object Das zu sendende Objekt
     */
    public void sendToEveryone(String stringToSend) {
        clientManagerMap.entrySet().stream().forEach((client) -> {
                client.sendToClient(stringToSend)
        });
    }

    /**
     * Gibt den ClientManager des jeweiigen Benutzers zurück
     *
     * @param id Der Benutzer, dessen Client gesucht wird
     * @return Der ClientManager des Benutzers
     */
    public Client getClient(String id) {
        return clientManagerMap.get(id);
    }

    /**
     * Entfernt einen Clienten von der Clientmanagerliste, z.B. falls er seine
     * Verbindung trennt
     *
     * @param client Der zu entfernende ClientManager
     */
    public void removeFromClientList(Client client) {
        clientManagerMap.remove(client.getUsername());
        clientManagerMap.remove(String.valueOf(client.getSocket().hashCode()));//Falls er nich nciht eingelogggt war
        iManager.getGUI().setListView();
    }

    public void addUsernameToMap(Client client) {
        clientManagerMap.remove(String.valueOf(client.getSocket().hashCode()));
        clientManagerMap.put(client.getUsername(), client);
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
        this.THREADPOOL = Executors.newCachedThreadPool();
        System.out.println("Server erreichbar unter " + SERVER.getInetAddress() + ":" + SERVER.getLocalPort());
        this.listenForClients();
    }

    private void onServerClosing(){
        
    }
    
    private void onServerStarted(){
        
    }
    
    private void onClientConnected(Client client){
        
    }
    
}
