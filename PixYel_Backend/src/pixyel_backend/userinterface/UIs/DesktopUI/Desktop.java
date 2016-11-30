/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.UIs.DesktopUI;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.Collection;
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

        VerticalLayout appConsole = getAppButtons(Translations.get(Translations.DESKTOP_CONSOLE), new ExternalResource("http://vaadin.com/image/user_male_portrait?img_id=44268&t=1251193981449"));
        gridLayout.addComponent(appConsole, 0, 0);
        gridLayout.setComponentAlignment(appConsole, Alignment.MIDDLE_CENTER);

        VerticalLayout appOnlineMonitor = getAppButtons(Translations.get(Translations.DESKTOP_ONLINE_MONITOR), new ExternalResource("http://vaadin.com/image/user_male_portrait?img_id=44268&t=1251193981449"));
        gridLayout.addComponent(appOnlineMonitor, 1, 0);
        gridLayout.setComponentAlignment(appOnlineMonitor, Alignment.MIDDLE_CENTER);

        VerticalLayout appUserManagement = getAppButtons(Translations.get(Translations.DESKTOP_USER_MANAGMENT), new ExternalResource("http://vaadin.com/image/user_male_portrait?img_id=44268&t=1251193981449"));
        gridLayout.addComponent(appUserManagement, 2, 0);
        gridLayout.setComponentAlignment(appUserManagement, Alignment.MIDDLE_CENTER);

        VerticalLayout appLogout = getAppButtons(Translations.get(Translations.DESKTOP_LOGOUT), new ExternalResource("http://vaadin.com/image/user_male_portrait?img_id=44268&t=1251193981449"));
        gridLayout.addComponent(appLogout, 3, 0);
        gridLayout.setComponentAlignment(appLogout, Alignment.MIDDLE_CENTER);
        return gridLayout;
    }

    private static VerticalLayout getAppButtons(String caption, Resource icon) {
        Button button = new Button();
        //button.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        button.setIcon(new ExternalResource("http://vaadin.com/image/user_male_portrait?img_id=44268&t=1251193981449"));
        button.addListener((ClickListener) (Button.ClickEvent event) -> {
            Notification.show("CLICK!");
        });
        button.setWidth("140px");
        button.setHeight("140px");

        Label label = new Label(Translations.get(Translations.DESKTOP_ONLINE_MONITOR));
        VerticalLayout verticalLayout = new VerticalLayout(button, label);
        verticalLayout.setSizeFull();
        verticalLayout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
        verticalLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        return verticalLayout;
    }

}
