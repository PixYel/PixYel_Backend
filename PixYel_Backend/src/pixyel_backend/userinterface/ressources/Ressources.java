/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.ressources;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.userinterface.Login;

/**
 *
 * @author Josua Frank
 */
public class Ressources {

    /**
     * Returns the requested ressource, if available
     * @param name the name of the ressource, e.g. image.png, languages.xml
     * @return the ressource as File
     * @throws pixyel_backend.userinterface.ressources.Ressources.RessourceNotFoundException If it wasnt possible to find the requested ressource
     */
    public static File getRessource(String name) throws RessourceNotFoundException {
        File file = new File(Ressources.class.getResource("/pixyel_backend/userinterface/ressources/" + name).getPath().replaceAll("%20", " "));
        if (file.exists()) {
            return file;
        } else {
            throw new RessourceNotFoundException("Could not find ressource: " + file.getPath().replaceAll("%20", " "));
        }
    }

    public static File addRessource(String name) throws RessourceCreationException {
        File newFile = new File(Ressources.class.getResource("/pixyel_backend/userinterface/ressources/" + name).getPath().replaceAll("%20", " "));
        if (!newFile.exists()) {
            try {
                newFile.createNewFile();
            } catch (IOException ex) {
                throw new RessourceCreationException("Could not create ressource: " + newFile.getPath().replaceAll("%20", " "));
            }
        }
        return newFile;
    }
    
        public static class RessourceNotFoundException extends Exception {

        public RessourceNotFoundException(String message) {
            super(message);
        }

    }

    public static class RessourceCreationException extends Exception {

        public RessourceCreationException(String message) {
            super(message);
        }

    }

}
