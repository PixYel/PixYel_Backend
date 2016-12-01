package pixyel_backend.userinterface.UIs.LoginUI;

import com.vaadin.event.ShortcutAction;
import pixyel_backend.userinterface.UIs.DesktopUI.Desktop;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.Log;
import pixyel_backend.database.exceptions.UserCreationException;
import pixyel_backend.database.objects.WebUser;
import pixyel_backend.userinterface.Translations;
import pixyel_backend.userinterface.ressources.Ressources;

public class Login {

    public static void show() {
        UI.getCurrent().setResizeLazy(false);
        Page.getCurrent().addBrowserWindowResizeListener((event) -> {
            Login.onResized();
        });

        VerticalLayout layout = new VerticalLayout();

        Component introVideo = getVideo();
        layout.addComponent(introVideo);
        layout.setSizeFull();
        layout.setComponentAlignment(introVideo, Alignment.MIDDLE_CENTER);

        UI.getCurrent().setContent(layout);
        showLoginPanel();
    }

    static Window loginWindow;
    static AbsoluteLayout loginForm;
    static Image logo;

    private static void showLoginPanel() {
        loginWindow = new Window();
        loginWindow.setResizeLazy(false);

        loginForm = new AbsoluteLayout();
        int WIDTH = (int) (0.3 * UI.getCurrent().getPage().getBrowserWindowWidth());
        int HIGHT = (int) (0.7 * UI.getCurrent().getPage().getBrowserWindowHeight());
        loginForm.setWidth(WIDTH, Unit.PIXELS);
        loginForm.setHeight(HIGHT, Unit.PIXELS);

        try {
            logo = new Image();
            logo.setSource(new FileResource(Ressources.getRessource("logo.png")));
            logo.setWidth((float) (WIDTH * 0.6), Unit.PIXELS);
            loginForm.addComponent(logo, "top:10%;left:" + ((WIDTH / 2) - (logo.getWidth() / 2)) + "px;");
        } catch (Ressources.RessourceNotFoundException e) {
            Log.logError("Could not find Logo for the loginscreen: " + e, Login.class);
        }

        final TextField txtUsername = new TextField();
        txtUsername.setInputPrompt(Translations.get(Translations.LOGIN_USERNAME));
        txtUsername.setSizeFull();
        loginForm.addComponent(txtUsername, "top: 40%; left: 10%; right: 10%; bottom: 50%");

        final PasswordField txtPassword = new PasswordField();
        txtPassword.setInputPrompt(Translations.get(Translations.LOGIN_PASSWORD));
        txtPassword.setSizeFull();
        loginForm.addComponent(txtPassword, "top: 60%; left: 10%; right: 10%; bottom: 30%");

        final Button btnLogin = new Button(Translations.get(Translations.LOGIN_LOGINBUTTON));
        btnLogin.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnLogin.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnLogin.addClickListener((listener) -> login(txtUsername.getValue(),  txtPassword.getValue()));
        btnLogin.setSizeFull();
        loginForm.addComponent(btnLogin, "top: 80%; left: 25%; right: 25%; bottom: 10%");

        loginWindow.setContent(loginForm);
        loginWindow.setResizable(false);
        loginWindow.center();
        loginWindow.setClosable(false);

        UI.getCurrent().addWindow(loginWindow);
    }

    public static void onResized() {
        if (loginWindow.isAttached()) {
            int WIDTH = (int) (0.3 * UI.getCurrent().getPage().getBrowserWindowWidth());
            int HIGHT = (int) (0.7 * UI.getCurrent().getPage().getBrowserWindowHeight());
            loginForm.setWidth(WIDTH, Unit.PIXELS);
            loginForm.setHeight(HIGHT, Unit.PIXELS);
            loginWindow.center();
            loginForm.removeComponent(logo);
            logo.setWidth((float) (WIDTH * 0.6), Unit.PIXELS);
            loginForm.addComponent(logo, "top:10%;left:" + ((WIDTH / 2) - (logo.getWidth() / 2)) + "px;");

        }
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
            Log.logError("Could not start Backgroundvideo: " + ex, Login.class);
            return null;
        }
    }

    public static void login(String username, String password) {
        boolean loginSuccessful = WebUser.loginWebUser(username, password);
        if (loginSuccessful) {
            Desktop.show();
        } else {
            Notification.show(Translations.get(Translations.LOGIN_WRONGLOGIN), Notification.Type.WARNING_MESSAGE);
        }

    }
}
