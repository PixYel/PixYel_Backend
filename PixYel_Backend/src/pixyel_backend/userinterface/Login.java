package pixyel_backend.userinterface;

import pixyel_backend.userinterface.Desktop.Desktop;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Video;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import pixyel_backend.Log;
import pixyel_backend.userinterface.ressources.Ressources;

public class Login {

    public static void show() {
        VerticalLayout layout = new VerticalLayout();

        Component introVideo = getVideo();
        layout.addComponent(introVideo);
        layout.setSizeFull();
        layout.setComponentAlignment(introVideo, Alignment.MIDDLE_CENTER);

        UI.getCurrent().setContent(layout);
        showLoginPanel();
    }

    private static void showLoginPanel() {
        Window loginWindow = new Window();

        final AbsoluteLayout loginForm = new AbsoluteLayout();
        int WIDTH = (int) (0.3 * UI.getCurrent().getPage().getBrowserWindowWidth());
        int HIGHT = (int) (0.7 * UI.getCurrent().getPage().getBrowserWindowHeight());
        loginForm.setWidth(WIDTH, Unit.PIXELS);
        loginForm.setHeight(HIGHT, Unit.PIXELS);

        try {
            Image image = new Image();
            image.setSource(new FileResource(Ressources.getRessource("logo.png")));
            image.setWidth((float) (WIDTH * 0.6), Unit.PIXELS);
            loginForm.addComponent(image, "top:10%;left:" + ((WIDTH / 2) - (image.getWidth() / 2)) + "px;");
        } catch (Ressources.RessourceNotFoundException e) {
            Log.logError("Could not find Logo for the loginscreen: " + e, Login.class);
        }

        final TextField txtUsername = new TextField();
        txtUsername.setInputPrompt("Username");
        txtUsername.setSizeFull();
        loginForm.addComponent(txtUsername, "top: 40%; left: 10%; right: 10%; bottom: 50%");

        final PasswordField txtPassword = new PasswordField();
        txtPassword.setInputPrompt("Password");
        txtPassword.setSizeFull();
        loginForm.addComponent(txtPassword, "top: 60%; left: 10%; right: 10%; bottom: 30%");

        final Button btnLogin = new Button("Login");
        btnLogin.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnLogin.addClickListener((listener) -> login());
        btnLogin.setSizeFull();
        loginForm.addComponent(btnLogin, "top: 80%; left: 25%; right: 25%; bottom: 10%");

        loginWindow.setContent(loginForm);
        loginWindow.setResizable(false);
        loginWindow.center();
        loginWindow.setClosable(false);

        UI.getCurrent().addWindow(loginWindow);
    }

    public static Component getVideo() {
        try {
            Video introVideo = new Video();
            File introVideoFile = Ressources.getRessource("login_background.mp4");
            final Resource mp4Resource = new FileResource(introVideoFile);
            introVideo.setSource(mp4Resource);
            introVideo.setSizeFull();
            introVideo.setAutoplay(true);
            introVideo.setShowControls(false);
            introVideo.setAltText("Cannot start our introvideo");
            return introVideo;
        } catch (Ressources.RessourceNotFoundException ex) {
            Log.logError("Coudl not start Backgroundvideo: " + ex, Login.class);
            return null;
        }
    }

    public static void login() {
        //BackendFunctions.login;
        boolean loginSuccessful = true;
        if (loginSuccessful) {
            Desktop.show();
        } else {
            Notification.show("Wrong username or password", Notification.Type.ERROR_MESSAGE);
        }

    }

    public String getHash(String username, String password) {
        String saltConstant = "#Schnitzel und #Dan-Pierres_erster_Pizzafleischkaes";
        MessageDigest md;
        StringBuilder sb = new StringBuilder();

        String saltedPW = password + saltConstant + username;

        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(saltedPW.getBytes());
            byte byteData[] = md.digest();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Algorithmus nicht verfügbar: " + ex.getMessage());
            return "";
        }
        return sb.toString();
    }
}
