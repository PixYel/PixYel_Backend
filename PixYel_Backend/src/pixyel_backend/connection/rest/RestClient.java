/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection.rest;

import pixyel_backend.Log;
import pixyel_backend.connection.Client;
import pixyel_backend.database.exceptions.UserCreationException;
import pixyel_backend.database.exceptions.UserNotFoundException;
import pixyel_backend.database.objects.User;

/**
 *
 * @author Josua Frank
 */
public class RestClient implements Client {

    private User userdata;
    private final String ip;

    public RestClient(String ip, String storeId) {
        this.ip = ip;
        try {
            userdata = User.getUser(storeId);
        } catch (UserNotFoundException | UserCreationException ex) {
            try {
                userdata = User.addNewUser(storeId);
            } catch (UserCreationException ex1) {
                Log.logError("Could not create new User: " + ex, RestClient.class);
                userdata = null;
            }
        }
    }

    @Override
    public String getName() {
        if (userdata != null) {
            return userdata.getStoreID();
        } else {
            return "[" + ip + "]";
        }
    }

    @Override
    public User getUserdata() {
        return userdata;
    }

    @Override
    public void disconnect(){
        RestServer.disconnect(this);
    }

}
