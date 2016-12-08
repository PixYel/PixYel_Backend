package pixyel_backend.connection.rest;


import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class RestServer implements Runnable {

    private static final int PORT = 7332;

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

    public static void main(String[] args) {
        RestServer.start();
    }

    @Override
    public void run() {
        try {
            Server server = configureServer();
            server.start();
            server.join();
        } catch (Exception ex) {
            Logger.getLogger(RestServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
