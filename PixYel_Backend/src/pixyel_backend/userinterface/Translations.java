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
    public static final int LANGUAGE_ENGLISH = -1;

    private static HashMap<Integer, String> german = new HashMap<>();
    public static final int LANGUAGE_GERMAN = -2;
    
    private static HashMap<Integer, String> french = new HashMap<>();

    public static final int LANGUAGE_French = -2;

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

    //Console
    public static final int CONSOLE_DATE = 200;
    public static final int CONSOLE_CLASS = 201;
    public static final int CONSOLE_MESSAGE = 202;

    //System Monitor
    public static final int SYSTEMMONITOR_NEWESTIMAGES = 300;
    public static final int SYSTEMMONITOR_TOPIMAGE = 301;
    public static final int SYSTEMMONITOR_SPECIALIMAGE = 302;

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
        english.put(CONSOLE_DATE, "Date");
        english.put(CONSOLE_CLASS, "Class");
        english.put(CONSOLE_MESSAGE, "Message");
        english.put(SYSTEMMONITOR_NEWESTIMAGES, "Newest Images");
        english.put(SYSTEMMONITOR_TOPIMAGE, "Top Images");
        english.put(SYSTEMMONITOR_SPECIALIMAGE, "Get Images by ID");

        //German
        german.put(LOGIN_LOGINBUTTON, "Anmelden");
        german.put(LOGIN_USERNAME, "Benutzername");
        german.put(LOGIN_PASSWORD, "Passwort");
        german.put(LOGIN_WRONGLOGIN, "Falscher Benutzername oder Passwort");
        german.put(DESKTOP_CONSOLE, "Konsole");
        german.put(DESKTOP_ONLINE_MONITOR, "Serverüberwachung");
        german.put(DESKTOP_USER_MANAGMENT, "Benutzerverwaltung");
        german.put(DESKTOP_LOGOUT, "Abmelden");
        german.put(CONSOLE_DATE, "Datum");
        german.put(CONSOLE_CLASS, "Klasse");
        german.put(CONSOLE_MESSAGE, "Nachricht");
        german.put(SYSTEMMONITOR_NEWESTIMAGES, "Neueste Bilder");
        german.put(SYSTEMMONITOR_TOPIMAGE, "Angesagteste Bilder");
        german.put(SYSTEMMONITOR_SPECIALIMAGE, "Hole Bilder per ID");
        
        //French
        french.put(LOGIN_LOGINBUTTON, "enregistrer");
        french.put(LOGIN_USERNAME, "nom d'utilisateur");
        french.put(LOGIN_PASSWORD, "mot de passe");
        french.put(LOGIN_WRONGLOGIN, "nom d'utilisateur ou mot de passe incorrect");
        french.put(DESKTOP_CONSOLE, "console");
        french.put(DESKTOP_ONLINE_MONITOR, "surveillance du serveur");
        french.put(DESKTOP_USER_MANAGMENT, "User Management");
        french.put(DESKTOP_LOGOUT, "Déconnexion");
        french.put(CONSOLE_DATE, "Date");
        french.put(CONSOLE_CLASS, "classe");
        french.put(CONSOLE_MESSAGE, "message");
        french.put(SYSTEMMONITOR_NEWESTIMAGES, "Nouvelles images");
        french.put(SYSTEMMONITOR_TOPIMAGE, "Hottest images");
        french.put(SYSTEMMONITOR_SPECIALIMAGE, "images trou par ID");
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

    public static String get(int KEYWORD, int LANGUAGE) {
        switch (LANGUAGE) {
            case LANGUAGE_ENGLISH:
                return english.get(KEYWORD);
            case LANGUAGE_GERMAN:
                return english.get(KEYWORD);
            default:
                return english.get(KEYWORD);
        }
    }

    public static String getDefault(int KEYWORD) {
        return get(KEYWORD, LANGUAGE_ENGLISH);
    }

}
