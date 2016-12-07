/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.UIs.DesktopUI;

import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/**
 *
 * @author Josua Frank
 */
public class OnlineMonitorWindow extends Window {

    public static void show(Runnable onClose) {
        new UserManagementWindow(onClose);
    }

    public OnlineMonitorWindow(Runnable onClose) {
        addCloseListener(ce -> onClose.run());

        UI.getCurrent().addWindow(this);
    }

}
