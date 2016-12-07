/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend;

import pixyel_backend.database.exceptions.UserCreationException;
import pixyel_backend.database.objects.WebUser;

/**
 *
 * @author i01frajos445
 */
public class UserinterfaceTest {
    
    public static void main(String[] args) throws UserCreationException {
        WebUser.deleteWebUser("Admin");
        WebUser.addNewWebUser("Admin", "minda");
        boolean loginWebUser = WebUser.loginWebUser("Admin", "minda");
        System.out.println(loginWebUser);
    }
    
}
