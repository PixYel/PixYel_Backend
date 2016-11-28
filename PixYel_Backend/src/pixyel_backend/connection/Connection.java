package pixyel_backend.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import pixyel_backend.Log;

/**
 *
 * @author Josua Frank
 */
public class Connection implements Runnable {

    private static ServerSocket SERVER;
    private static final ExecutorService CLIENTTHREADPOOL = Executors.newCachedThreadPool();

    private static final HashMap<Integer, Client> CONNECTEDCLIENTS = new HashMap<>();//All online clients
    private static final HashMap<String, Client> LOGGEDINCLIENTS = new HashMap<>();//All logged in clients

    /**
     * Here the server is going to be started.
     *
     */
    public static void start() {
        Executors.newSingleThreadExecutor().submit(new Connection());
    }

    /**
     * Removes the client from the list of connected clients
     *
     * @param socketHashCode The Hash code of the socket as key of the map
     */
    public static void disconnect(int socketHashCode) {
        onClientDisconnected(CONNECTEDCLIENTS.get(socketHashCode));
        CONNECTEDCLIENTS.remove(socketHashCode);
    }

    /**
     * Searches infinietly for clients, when a client is connected, it gets its
     * own thread
     */
    private static void listenForClients() {
        boolean loop = true;
        Socket socket;
        while (loop) {
            try {
                socket = SERVER.accept();
                //socket.setSoTimeout(5000);
                Client client = new Client(socket);
                CONNECTEDCLIENTS.put(socket.hashCode(), client);
                CLIENTTHREADPOOL.submit(client);
                onClientConnected(client);
            } catch (Exception e) {
                Log.logError("IO Error occured during the setup of the connection to the client: " + e, Connection.class);
                loop = false;
            }
        }
    }

    /**
     * Should be called right after a client has logged in
     *
     * @param client The new Client which has logged in
     */
    public static void removePossibleDoubleClients(Client client) {
        if (LOGGEDINCLIENTS.containsKey(client.getName())) {
            Log.logInfo("Removing old client from " + client.getName(), Connection.class);
            LOGGEDINCLIENTS.get(client.getName()).disconnect(true);
        }
        LOGGEDINCLIENTS.put(client.getName(), client);
    }

    /**
     * Stops the server
     *
     */
    public static void stopServer() {
        if (SERVER != null) {
            try {
                onServerClosing();
                CLIENTTHREADPOOL.shutdown();
                SERVER.close();
                System.exit(0);
            } catch (IOException e) {
                Log.logError("Socket could not be closed: " + e.getMessage(), Connection.class);
            }
        }
    }

    /**
     * Startes the server, use {@code Connection.start()} !!
     */
    @Override
    public void run() {
        try {
            SERVER = new ServerSocket(7331);
        } catch (java.net.BindException e) {
            Log.logError("Adress already binded, is there an existing server running?: " + e.getMessage(), this);
            Log.logError("Shutting down this server to prevent double servers!", this);
            System.exit(0);
            return;
        } catch (IOException ex) {
            Log.logError("Server could not be started: " + ex.getMessage(), this);
            System.exit(0);
            return;
        }
        onServerStarted();
        listenForClients();
    }

    /**
     * Calls this Method just before its closing its socket
     */
    private static void onServerClosing() {
        Log.logInfo("Shutting down the Server...", Connection.class);
    }

    /**
     * Calls this Method right after it initialized iteself but before its
     * accepting new clients
     */
    private static void onServerStarted() {
        Log.logInfo("Server reachable on " + SERVER.getLocalSocketAddress() + ":" + SERVER.getLocalPort(), Connection.class);
    }

    /**
     * Calls this method right after the socket connection from a new client is
     * established, the client runs in a seperate thread
     *
     * @param client The client which is connected
     */
    private static void onClientConnected(Client client) {
        Log.logInfo("Client " + client.getName() + " connected", Connection.class);
    }

    /**
     * Calls this method just before its closing the socket of the client
     *
     * @param client The client which is going to be disconnected
     */
    private static void onClientDisconnected(Client client) {
        Log.logInfo("Client " + client.getName() + " disconnected", Connection.class);
    }

}
