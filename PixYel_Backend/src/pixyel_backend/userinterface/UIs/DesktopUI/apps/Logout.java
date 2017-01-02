/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.UIs.DesktopUI.apps;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.util.Collection;
import pixyel_backend.userinterface.UIs.LoginUI.Login;

/**
 *
 * @author Josua Frank
 */
public class Logout {

    public static void show() {
        closeAllWindows();
        Login.show();
    }

    public static void closeAllWindows() {
        Collection<Window> windows = UI.getCurrent().getWindows();
        UI ui = UI.getCurrent();
        windows.stream().forEach(ui::removeWindow);
    }
}
