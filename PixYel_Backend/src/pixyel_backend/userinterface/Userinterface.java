package pixyel_backend.userinterface;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import pixyel_backend.Log;
import pixyel_backend.Main;
import pixyel_backend.userinterface.UIs.LoginUI.Login;

/**
 * This Userinterface is the application entry point. A Userinterface may either
 * represent a browser window (or tab) or some part of a html page where a
 * Vaadin application is embedded.
 * <p>
 * The Userinterface is initialized using {@link #init(VaadinRequest)}. This
 * method is intended to be overridden to add component to the user interface
 * and initialize non-component functionality.
 */
@Theme("mytheme")
public class Userinterface extends com.vaadin.ui.UI implements Runnable {

    private final static int PORT = 8080;
    private static Runnable onStarted;
    private static boolean started = false;

    /**
     * To start the Userinterface in general as a separate Thread
     */
    public static void start() {
        Translations.init();
        Executors.newCachedThreadPool().submit(new Userinterface());
    }

    private static VaadinJettyServer vaadinJettyServer;

    /**
     * Here starts the seperate Thread working
     */
    @Override
    public void run() {
        try {
            vaadinJettyServer = new VaadinJettyServer(PORT, Userinterface.class);
            vaadinJettyServer.start();
            if (onStarted != null) {
                onStarted.run();
            }
            started = true;
        } catch (Exception ex) {
            if (ex.toString().contains("Address already in use")) {
                System.err.println("Could not start UI, port " + PORT + " is used by another program, shutting down this UI");
                //TODO Shutdown without Userinterface Server shutdown
            } else {
                System.err.println("Could not start UI: " + ex);
            }

        }
    }

    /**
     * Here is the first UI being loaded
     *
     * @param vaadinRequest
     */
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setImmediate(true);
        Login.show();
    }

    /**
     * Some settings for the VaadinServlet and other things
     */
    @WebServlet(urlPatterns = "/*", name = "PixYelUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = Userinterface.class, productionMode = !Main.DEBUG)
    public static class PixYelUIServlet extends VaadinServlet {
    }

    public static void onStarted(Runnable r) {
        if (started) {
            r.run();
        } else {
            onStarted = r;
        }
    }

    /**
     * ONLY for DEBUGGING Reasons
     *
     * @param args
     */
    public static void main(String[] args) {
        start();
    }

}
