package pixyel_backend.userinterface;

import pixyel_backend.userinterface.Desktop.Desktop;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Video;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;

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

        final TextField txtUsername = new TextField();
        txtUsername.setInputPrompt("Username");
        final PasswordField txtPassword = new PasswordField();
        txtPassword.setInputPrompt("Password");
        final Button btnLogin = new Button("Login");
        btnLogin.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnLogin.addClickListener((listener) -> Desktop.show());

        //Sternchen ist hässlich
        //txtUsername.setRequired(true);
        //txtPassword.setRequired(true);
        final Panel loginPanel = new Panel();
        loginPanel.setWidth(null);

        final FormLayout loginForm = new FormLayout();
        Image image = new Image();
        image.setSource(new FileResource(new File(System.getProperty("user.home") + "\\Desktop\\logo.png")));
        image.setHeight("50");
        loginForm.addComponent(image);
        loginForm.setMargin(true);
        loginForm.addComponent(txtUsername);
        loginForm.addComponent(txtPassword);
        loginForm.addComponent(btnLogin);

        loginPanel.setContent(loginForm);
        loginWindow.setContent(loginPanel);
        loginWindow.setResizable(false);
        loginWindow.center();
        loginWindow.setClosable(false);

        UI.getCurrent().addWindow(loginWindow);
    }

    public static Component getVideo() {
        Video introVideo = new Video();
        File introVideoFile = new File(Login.class.getResource("/pixyel_backend/userinterface/ressources/login_background.mp4").getPath().replace("%20", " "));
        final Resource mp4Resource = new FileResource(introVideoFile);
        introVideo.setSource(mp4Resource);
        introVideo.setSizeFull();
        introVideo.setAutoplay(true);
        introVideo.setShowControls(false);
        introVideo.setAltText("Cannot start our introvideo");
        return introVideo;
    }
}
