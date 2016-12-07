/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.UIs.DesktopUI;

import com.vaadin.event.MouseEvents;
import com.vaadin.server.FileResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.Log;
import pixyel_backend.userinterface.Translations;
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
        layout.addComponent(getTaskBar(), "top: 80%; left: 0%; right: 0%, bottom: 0%");

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
            Log.logError("Could not set the background for the desktop: " + ex, Desktop.class);
        }
        image.setSizeFull();
        return image;
    }

    private static Component getTaskBar() {
        GridLayout gridLayout = new GridLayout(4, 1);
        gridLayout.setSizeFull();

        GridLayout appConsole = getAppButtons(Translations.get(Translations.DESKTOP_CONSOLE), "desktop_console_icon.png");
        gridLayout.addComponent(appConsole, 0, 0);
        gridLayout.setComponentAlignment(appConsole, Alignment.MIDDLE_CENTER);

        GridLayout appOnlineMonitor = getAppButtons(Translations.get(Translations.DESKTOP_ONLINE_MONITOR), "desktop_console_icon.png");
        gridLayout.addComponent(appOnlineMonitor, 1, 0);
        gridLayout.setComponentAlignment(appOnlineMonitor, Alignment.MIDDLE_CENTER);

        GridLayout appUserManagement = getAppButtons(Translations.get(Translations.DESKTOP_USER_MANAGMENT), "desktop_console_icon.png");
        gridLayout.addComponent(appUserManagement, 2, 0);
        gridLayout.setComponentAlignment(appUserManagement, Alignment.MIDDLE_CENTER);

        GridLayout appLogout = getAppButtons(Translations.get(Translations.DESKTOP_LOGOUT), "desktop_console_icon.png");
        gridLayout.addComponent(appLogout, 3, 0);
        gridLayout.setComponentAlignment(appLogout, Alignment.MIDDLE_CENTER);
        return gridLayout;
    }

    private static GridLayout getAppButtons(String caption, String ressourceName) {
        Image image = new Image();
        try {
            image.setSource(new FileResource(Ressources.getRessource(ressourceName)));
        } catch (Ressources.RessourceNotFoundException ex) {
            Logger.getLogger(Desktop.class.getName()).log(Level.SEVERE, null, ex);
        }
        image.addClickListener((MouseEvents.ClickEvent c) -> {
            Notification.show("Console");
        });

        Label label = new Label(caption);
        label.setSizeUndefined();
        label.setStyleName(ValoTheme.LABEL_COLORED);
        GridLayout gridLayout = new GridLayout(1, 2);
        gridLayout.addComponent(image, 0, 0);
        gridLayout.addComponent(label, 0, 1);
        gridLayout.setComponentAlignment(image, Alignment.MIDDLE_CENTER);
        gridLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        return gridLayout;
    }

}
