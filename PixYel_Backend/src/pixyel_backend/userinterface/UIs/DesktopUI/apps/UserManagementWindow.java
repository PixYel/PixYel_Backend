/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.UIs.DesktopUI.apps;

import com.vaadin.server.FileResource;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.database.exceptions.UserCreationException;
import pixyel_backend.database.objects.User;
import pixyel_backend.userinterface.ressources.Ressources;

/**
 *
 * @author Josua Frank
 */
public class UserManagementWindow extends Window {

    public static void show(Runnable onClose) {
        new UserManagementWindow(onClose);
    }

    public UserManagementWindow(Runnable onClose) {
        addCloseListener(ce -> onClose.run());
        try {
            setIcon(new FileResource(Ressources.getRessource("desktop_user_management_icon_small.png")));
        } catch (Ressources.RessourceNotFoundException ex) {
        }

        try {
            User.getAllUsers();
        } catch (UserCreationException ex) {
            Logger.getLogger(UserManagementWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        UI.getCurrent().addWindow(this);
    }

}
