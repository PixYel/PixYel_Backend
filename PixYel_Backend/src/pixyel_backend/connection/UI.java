/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import java.util.HashMap;
import pixyel_backend.xml.XML;

/**
 *
 * @author Josua Frank
 */
public class UI {

    private static Client ui;
    private static final HashMap<String, String> LOGINS = new HashMap<>();

    public static boolean login(Client client, String username, String password) {
        LOGINS.put("Sharknoon", "backend");
        if (LOGINS.containsKey(username) && LOGINS.get(username).equals(password)) {
            ui = client;
            return true;
        } else {
            return false;
        }
    }

    public static void send(XML toSend) {
        if (ui != null) {
            ui.sendToClient(toSend);
        }
    }

}
