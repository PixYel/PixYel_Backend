package pixyel_backend.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import pixyel_backend.Log;

/**
 *
 * @author Josua Frank
 */
public class Connection implements Runnable {

    private static ServerSocket SERVER = null;
    private static final HashMap<Integer, Client> CONNECTEDCLIENTS = new HashMap<>();//All online clients
    private static final ExecutorService CLIENTTHREADPOOL = Executors.newCachedThreadPool();

    /**
     * Here the server is going to be started.
     *
     */
    public static void start() {
        Executors.newFixedThreadPool(1).submit(new Connection());
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
     * Returns all online Clients
     *
     * @return A ArrayList containing all online Clients
     */
    public static ArrayList<Client> getAllOnlineClients() {
        ArrayList<Client> result = new ArrayList<>();
        CONNECTEDCLIENTS.entrySet().stream().forEach((clientIdPair) -> {
            result.add(clientIdPair.getValue());
        });
        return result;
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
        } catch (IOException ex) {
            Log.logError("Server could not be started: " + ex.getMessage(), this);
        }
        onServerStarted();
        startClientAliveScheduler();
        Connection.listenForClients();
    }

    /**
     * Starts the Scheduler for the aliveCheck
     */
    public void startClientAliveScheduler() {
        Executors.newFixedThreadPool(1).submit(() -> {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    CONNECTEDCLIENTS.forEach((hashcode, client) -> {
                        client.checkClientAlive(false);
                    });
                }
            }, 120000);
        });
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
