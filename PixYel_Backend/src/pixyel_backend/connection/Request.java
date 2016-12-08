/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("pixyel")
public class Request {
  @GET
  @Path("getItem")
  @Produces(MediaType.TEXT_PLAIN)
  public String helloWorld() {
    return "Das wird ein Bild";
  }
}