/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.Desktop;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.util.Collection;
import org.vaadin.addon.borderlayout.BorderLayout;

/**
 *
 * @author Josua Frank
 */
public class Desktop {

    private static BorderLayout layout = new BorderLayout();

    public static void show() {
        Notification.show("Showing Desktop");
        closeAllWindows();



        layout.setSizeFull();
        UI.getCurrent().setContent(layout);
    }

    public static void closeAllWindows() {
        Collection<Window> windows = UI.getCurrent().getWindows();
        UI ui = UI.getCurrent();
        windows.stream().forEach(ui::removeWindow);
    }

    public static void initSideMenue() {
        CssLayout sidebarLayout = new CssLayout();

        //Title
        Label logo = new Label("<strong>PixYel<strong>\nBackend", ContentMode.HTML);
        HorizontalLayout logoWrapper = new HorizontalLayout(logo);
        logoWrapper.setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
        logoWrapper.addStyleName("valo-menu-title");
        sidebarLayout.addComponent(logo);

        //User Menue
//        final MenuBar settings = new MenuBar();
//        settings.addStyleName("user-menu");
//        final User user = getCurrentUser();
//        MenuItem settingsItem = settings.addItem("", new ThemeResource("img/profile-pic-300px.jpg"), null);
//        updateUserName(null);
//        settingsItem.addItem("Edit Profile", new Command() {
//            @Override
//            public void menuSelected(final MenuItem selectedItem) {
//                //ProfilePreferencesWindow.open(user, false);
//            }
//        });
//        settingsItem.addItem("Preferences", new Command() {
//            @Override
//            public void menuSelected(final MenuItem selectedItem) {
//                ProfilePreferencesWindow.open(user, true);
//            }
//        });
//        settingsItem.addSeparator();
//        settingsItem.addItem(Language.SIDE_MENUE_LOG_OUT[Language.DEFAULTLANGUAGE], new Command() {
//            @Override
//            public void menuSelected(final MenuItem selectedItem) {
//                DashboardEventBus.post(new UserLoggedOutEvent());
//            }
//        });
    }

}
