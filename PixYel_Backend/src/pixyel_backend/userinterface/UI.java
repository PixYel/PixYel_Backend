package pixyel_backend.userinterface;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class UI extends com.vaadin.ui.UI {

    private final static int PORT = 8080;

    public static void start() {
        try {
            VaadinJettyServer vaadinJettyServer = new VaadinJettyServer(8080, UI.class);
            vaadinJettyServer.start();
        } catch (Exception ex) {
            if (ex.toString().contains("Address already in use")) {
                System.err.println("Could not start UI, port " + PORT + " is used by another program, shutting down this UI");
                //TODO Shutdown without UI Server shutdown
            } else {
                System.err.println("Could not start UI: " + ex);
            }

        }
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Login.show();
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = UI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

    public static void main(String[] args) {
        start();
    }

}
