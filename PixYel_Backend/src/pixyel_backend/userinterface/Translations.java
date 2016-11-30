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
    public static final int DESKTOP_CONSOLE = 100;
    public static final int DESKTOP_ONLINE_MONITOR = 101;
    public static final int DESKTOP_USER_MANAGMENT = 102;
    public static final int DESKTOP_LOGOUT = 103;

    public static void init() {
        //**********************************//
        //           TRANSLATIONS           //
        //**********************************//
        //English
        english.put(LOGIN_LOGINBUTTON, "Login");
        english.put(LOGIN_USERNAME, "Username");
        english.put(LOGIN_PASSWORD, "Password");
        english.put(LOGIN_WRONGLOGIN, "Wrong Username or password");
        english.put(DESKTOP_CONSOLE, "Console");
        english.put(DESKTOP_ONLINE_MONITOR, "Online Monitor");
        english.put(DESKTOP_USER_MANAGMENT, "User Management");
        english.put(DESKTOP_LOGOUT, "Log out");

        //German
        german.put(LOGIN_LOGINBUTTON, "Anmelden");
        german.put(LOGIN_USERNAME, "Benutzername");
        german.put(LOGIN_PASSWORD, "Passwort");
        german.put(LOGIN_WRONGLOGIN, "Falscher Benutzername oder Passwort");
        german.put(DESKTOP_CONSOLE, "Konsole");
        german.put(DESKTOP_ONLINE_MONITOR, "Serverüberwachung");
        german.put(DESKTOP_USER_MANAGMENT, "Benutzerverwaltung");
        german.put(DESKTOP_LOGOUT, "Abmelden");
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
