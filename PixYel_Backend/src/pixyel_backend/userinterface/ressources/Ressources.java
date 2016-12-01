/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.ressources;

import java.io.File;

/**
 *
 * @author Josua Frank
 */
public class Ressources {

    /**
     * Returns the requested ressource, if available
     *
     * @param name the name of the ressource, e.g. image.png, languages.xml
     * @return the ressource as File
     * @throws
     * pixyel_backend.userinterface.ressources.Ressources.RessourceNotFoundException
     * If it wasnt possible to find the requested ressource
     */
    public static File getRessource(String name) throws RessourceNotFoundException {
        try {
            File file = new File(Ressources.class.getResource("/pixyel_backend/userinterface/ressources/" + name).getPath().replaceAll("%20", " "));
            if (file.exists()) {
                return file;
            } else {
                throw new RessourceNotFoundException("Could not find ressource: " + file.getPath().replaceAll("%20", " "));
            }
        } catch (Exception e) {
            throw new RessourceNotFoundException("Could not find ressource: " + name);
        }
    }

    public static class RessourceNotFoundException extends Exception {

        public RessourceNotFoundException(String message) {
            super(message);
        }

    }

}
