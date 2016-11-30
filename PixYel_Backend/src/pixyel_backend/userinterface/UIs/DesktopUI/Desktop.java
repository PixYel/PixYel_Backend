/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.UIs.DesktopUI;

import com.vaadin.server.FileResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.util.Collection;
import pixyel_backend.Log;
import pixyel_backend.userinterface.ressources.Ressources;

/**
 *
 * @author Josua Frank
 */
public class Desktop {

    private static AbsoluteLayout layout = new AbsoluteLayout();

    public static void show() {
        //Notification.show("Showing Desktop");
        closeAllWindows();
        layout.addComponent(getImage(), "top:0px; left: 0px;");

        layout.setSizeFull();
        UI.getCurrent().setContent(layout);
    }

    public static void closeAllWindows() {
        Collection<Window> windows = UI.getCurrent().getWindows();
        UI ui = UI.getCurrent();
        windows.stream().forEach(ui::removeWindow);
    }

    private static Image getImage() {
        Image image = new Image();
        try {
            image.setSource(new FileResource(Ressources.getRessource("desktop_background.jpg")));
        } catch (Ressources.RessourceNotFoundException ex) {
            Log.logError("Could not set the background for the desktop: ", Desktop.class);
        }
        image.setSizeFull();
        return image;
    }

}
