package pixyel_backend.userinterface;

import pixyel_backend.userinterface.Desktop.Desktop;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Video;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
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
        int WIDTH = 400;
        int HIGHT = 500;
        loginForm.setWidth(WIDTH, Sizeable.Unit.PIXELS);
        loginForm.setHeight(HIGHT, Sizeable.Unit.PIXELS);
        
        final TextField txtUsername = new TextField();
        txtUsername.setInputPrompt("Username");
        txtUsername.setWidth("top:10%;left:" + ((5 / 4) * WIDTH) + "px;");
        loginForm.addComponent(txtUsername, "");
        
        final PasswordField txtPassword = new PasswordField();
        txtPassword.setInputPrompt("Password");
        final Button btnLogin = new Button("Login");
        btnLogin.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnLogin.addClickListener((listener) -> Desktop.show());
        
        try {
            Image image = new Image();
            image.setSource(new FileResource(Ressources.getRessource("logo.png")));
            image.setWidth("50");
            loginForm.addComponent(image, "top:10%;left:" + ((WIDTH / 2) - (image.getWidth() / 2)) + "px;");
        } catch (Ressources.RessourceNotFoundException e) {
            Log.logError("Could not find Logo for the loginscreen: " + e, Login.class);
        }

        //loginForm.setMargin(true);
        loginForm.addComponent(txtPassword);
        loginForm.addComponent(btnLogin);
        //loginForm.setComponentAlignment(btnLogin, Alignment.MIDDLE_CENTER);

        //loginPanel.setContent(loginForm);
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
}
