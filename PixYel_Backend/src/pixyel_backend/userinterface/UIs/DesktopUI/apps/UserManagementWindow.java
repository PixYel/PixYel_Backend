/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.UIs.DesktopUI.apps;

import com.vaadin.server.FileResource;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.database.exceptions.UserCreationException;
import pixyel_backend.database.objects.User;
import pixyel_backend.userinterface.Translations;
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

        setContent(getLayout());
        center();
        setCaption(" " + Translations.get(Translations.DESKTOP_USER_MANAGMENT));
        UI.getCurrent().addWindow(this);
    }

    public Layout getLayout() {
        Table table = new Table(" Users");

        // Define two columns for the built-in container
        table.addContainerProperty("ID", Integer.class, null);
        table.addContainerProperty("StoreID", String.class, null);
        table.addContainerProperty("Uploaded Pictures", Integer.class, null);
        table.addContainerProperty("Registration Date", DateField.class, null);
        table.addContainerProperty("Banned", Boolean.class, null);

        try {
            List<User> allUsers = User.getAllUsers();
            allUsers.sort((User o1, User o2) -> {
                if (o1.getID() > o2.getID()) {
                    return 1;
                } else if(o1.getID() < o2.getID()){
                    return -1;
                } else{
                    return 0;
                }
            });

            for (User user : allUsers) {
                Date regDate = new Date(user.getRegistrationDate().getTime());

                // Add a few other rows using shorthand addItem()
                table.addItem(new Object[]{
                    user.getID(),
                    user.getStoreID(),
                    user.getOwnPictures().size(),
                    new DateField("", regDate),
                    user.isBanned()}, allUsers.indexOf(user));
            }

            // Show exactly the currently contained rows (items)
            table.setPageLength(table.size());
        } catch (UserCreationException ex) {
        }
        return new VerticalLayout(table);
    }

}
