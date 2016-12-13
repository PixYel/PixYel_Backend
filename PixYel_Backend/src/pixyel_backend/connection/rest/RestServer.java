package pixyel_backend.connection.rest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import pixyel_backend.Log;

public class RestServer implements Runnable {

    private static final int PORT = 7332;
    private static final HashMap<String, RestClient> clients = new HashMap<>();

    public static void main(String[] args) {
        RestServer.start();
    }
    
    public static void start() {
        Executors.newSingleThreadExecutor().submit(new RestServer());
    }

    private Server configureServer() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages(Request.class.getPackage().getName());
        resourceConfig.register(JacksonFeature.class);
        ServletContainer servletContainer = new ServletContainer(resourceConfig);
        ServletHolder sh = new ServletHolder(servletContainer);
        Server server = new Server(PORT);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(sh, "/*");
        server.setHandler(context);
        return server;
    }

    @Override
    public void run() {
        try {
            Server server = configureServer();
            server.start();
            server.join();
            onServerStarted();
        } catch (Exception ex) {
            Logger.getLogger(RestServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static RestClient getOrCreateClient(String storeId, String ip) {
        if (clients.containsKey(storeId)) {
            return clients.get(storeId);
        } else {
            RestClient client = new RestClient(ip, storeId);
            clients.put(storeId, client);
            onClientConnected(client);
            return client;
        }
    }

    public static void disconnect(RestClient client) {
        clients.remove(client.getName());
        onClientDisconnected(client);
    }

    /**
     * Stops the server
     *
     */
    public static void stopServer() {
        onServerClosing();

    }

    /**
     * Calls this Method just before its closing its socket
     */
    private static void onServerClosing() {
        Log.logInfo("Shutting down the Server...", RestServer.class);
    }

    /**
     * Calls this Method right after it initialized iteself but before its
     * accepting new clients
     */
    private static void onServerStarted() {
        try {
            Log.logInfo("Restserver reachable on " + InetAddress.getLocalHost().toString() + ":" + PORT, RestServer.class);
        } catch (UnknownHostException ex) {
            Log.logInfo("Restserver reachable", RestServer.class);
        }
    }

    /**
     * Calls this method right after the socket connection from a new client is
     * established, the client runs in a seperate thread
     *
     * @param client The client which is connected
     */
    private static void onClientConnected(RestClient client) {
        Log.logInfo("Client " + client.getName() + " connected", RestServer.class);
    }

    /**
     * Calls this method just before its closing the socket of the client
     *
     * @param client The client which is going to be disconnected
     */
    private static void onClientDisconnected(RestClient client) {
        Log.logInfo("Client " + client.getName() + " disconnected", RestServer.class);
    }

}
