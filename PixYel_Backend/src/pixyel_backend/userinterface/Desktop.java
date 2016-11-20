/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface;

import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.io.File;
import java.util.Collection;
import static pixyel_backend.userinterface.Login.getVideo;

/**
 *
 * @author Josua Frank
 */
public class Desktop {

    private static AbsoluteLayout layout;
    
    public static void show() {
        Notification.show("showing Desktop");
        closeAllWindows();
        layout = new AbsoluteLayout();

        Component introVideo = getVideo();
        layout.addComponent(introVideo);
        layout.setSizeFull();

        UI.getCurrent().setContent(layout);
    }

    public static void closeAllWindows() {
        Collection<Window> windows = UI.getCurrent().getWindows();
        UI ui = UI.getCurrent();
        windows.stream().forEach(ui::removeWindow);
    }

    public void setBackgroundImage(){
        Panel backgroundImagePanel = new Panel();
        Image backgroundImage = new Image();
        File imageFile = new File("C:\\background.png");
        Resource imageRessource = new FileResource(imageFile);
        backgroundImage.setSource(imageRessource);
        backgroundImage.setSizeFull();
        backgroundImagePanel.setContent(backgroundImage);
        layout.addComponent(backgroundImagePanel, "top: 50%; left: 50%");
    }
    
}
