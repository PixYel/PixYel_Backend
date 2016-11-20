/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface;

import java.io.File;
import java.util.HashMap;
import pixyel_backend.Log;
import pixyel_backend.xml.XML;

/**
 *
 * @author Josua Frank
 */
public class Language {

    public static String DEFAULTLANGUAGE = "english";

    HashMap<String, HashMap<String,String>> content = new HashMap<>();//language(element, translation)

    public static void start() {
        File languagesXML = new File(System.getProperty("user.dir") + "//languages.xml");
        XML languages;
        if (!languagesXML.exists()) {
            languages = XML.createNewXML(languagesXML, "languages");
            languages.setAutosave(true);
            languages.addChildren("defaultlanguage");
            languages.getFirstChild().setContent("english");
            languages.addChild("language");
            languages.getFirstChild("language").addAttribute("name", "english");
            //languages.get
        } else {
            try {
                languages = XML.openXML(languagesXML);
            } catch (XML.XMLException ex) {
                Log.logError("Language: Language File may be corrupt: " + ex, Language.class);
                return;
            }
        }
        
        
    }
}
