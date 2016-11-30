/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.Log;
import pixyel_backend.userinterface.ressources.Ressources;
import pixyel_backend.xml.XML;

/**
 *
 * @author Josua Frank
 */
public class Languages {

    public static String DEFAULTLANGUAGE = "english";

    public static void start() {
        File languagesFile;
        try {
            languagesFile = Ressources.getRessource("languages.xml");
        } catch (Ressources.RessourceNotFoundException ex) {
            try {
                languagesFile = Ressources.addRessource("languages.xml");
            } catch (Ressources.RessourceCreationException ex1) {
                Log.logError("Could not initiate Languages: " + ex, Languages.class);
                return;
            }
        }
        XML languages;
        if (languagesFile.length() != 0) {
            languages = XML.createNewXML(languagesFile, "languages");
            languages.setAutosave(true);
            languages.addChild("defaultlanguage").setContent("english");
            languages.addChild("language").addAttribute("name", "english");
            languages.addChild("language").addAttribute("name", "german");
        } else {
            try {
                languages = XML.openXML(languagesFile);
                DEFAULTLANGUAGE = languages.getFirstChild("defaultlanguage").getContent();
            } catch (XML.XMLException ex) {
                Log.logError("Language: Language File may be corrupt: " + ex, Languages.class);
            }
        }

    }
}
