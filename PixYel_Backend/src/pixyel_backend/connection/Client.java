/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import pixyel_backend.database.objects.User;

/**
 *
 * @author Josua Frank
 */
public interface Client {

    public String getName();
    public User getUserdata();
    public void disconnect();

}
