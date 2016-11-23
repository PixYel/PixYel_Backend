/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.ressources;

import java.io.File;
import pixyel_backend.userinterface.Login;

/**
 *
 * @author Josua Frank
 */
public class Ressources {

    public static File getRessource(String name) throws RessourceNotFoundException {
        File file = new File(Login.class.getResource("/pixyel_backend/userinterface/ressources/" + name).getPath().replaceAll("%20", " "));
        if (file.exists()) {
            return file;
        } else {
            throw new RessourceNotFoundException("Could not find ressource: " + file.getPath().replaceAll("%20", " "));
        }
    }

    public static class RessourceNotFoundException extends Exception {

        public RessourceNotFoundException(String message) {
            super(message);
        }

    }

}
