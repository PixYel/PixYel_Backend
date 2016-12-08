package pixyel_backend.connection.rest;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pixyel_backend.xml.XML;

@Path("/api")
public class Request {

    @POST
    @Path("/request")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello(@DefaultValue("<EMPTY/>") @FormParam("xml") String xml) {

        try {
            return XML.createNewXML("FROMSERVER").addChild(XML.openXML(xml)).toString();
        } catch (XML.XMLException ex) {
            return "fehler: " + ex;
        }
    }

}
