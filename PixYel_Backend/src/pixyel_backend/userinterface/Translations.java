/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface;

import com.vaadin.ui.UI;
import java.util.HashMap;

/**
 *
 * @author Josua Frank
 */
public class Translations {

    //**********************************//
    //           LANGUAGES              //
    //**********************************//
    private static HashMap<Integer, String> english = new HashMap<>();
    private static HashMap<Integer, String> german = new HashMap<>();

    //**********************************//
    //           KEYWORDS               //
    //**********************************//
    //LOGIN
    public static final int LOGIN_LOGINBUTTON = 0;
    public static final int LOGIN_USERNAME = 1;
    public static final int LOGIN_PASSWORD = 2;
    public static final int LOGIN_WRONGLOGIN = 3;

    //Desktop
    public static void init() {
        //**********************************//
        //           TRANSLATIONS           //
        //**********************************//
        //English
        english.put(LOGIN_LOGINBUTTON, "Login");
        english.put(LOGIN_USERNAME, "Username");
        english.put(LOGIN_PASSWORD, "Password");
        english.put(LOGIN_WRONGLOGIN, "Wrong Username or password");

        //German
        german.put(LOGIN_LOGINBUTTON, "Anmelden");
        german.put(LOGIN_USERNAME, "Benutzername");
        german.put(LOGIN_PASSWORD, "Passwort");
        german.put(LOGIN_WRONGLOGIN, "Falscher Benutzername oder Passwort");
    }

    public static String get(int KEYWORD) {
        String locale;
        switch (locale = UI.getCurrent().getSession().getLocale().getLanguage().toLowerCase()) {
            case "en":
                return english.get(KEYWORD);
            case "de":
                return german.get(KEYWORD);
            default:
                System.err.println("Could not find LANGUAGE: " + locale);
                return english.get(KEYWORD);
        }
    }

}
