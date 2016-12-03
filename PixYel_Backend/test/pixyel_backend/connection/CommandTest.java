/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pixyel_backend.database.exceptions.UserCreationException;
import pixyel_backend.database.exceptions.UserNotFoundException;
import pixyel_backend.database.objects.User;
import pixyel_backend.xml.XML;

/**
 *
 * @author Administrator
 */
public class CommandTest {

    public CommandTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        try {
            dummyclient = new Client(new Socket(InetAddress.getByName("sharknoon.de"), 7331));
            dummyclient.setUserdata(User.getUser(1));
        } catch (UserNotFoundException | UserCreationException | IOException ex) {
            System.err.println("Could not create dummyclient: " + ex);
        }
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    static Client dummyclient;

    /**
     * Test of getItemList method, of class Command.
     */
    @Test
    public void testGetItemList() {
        XML toTest;
        String input = "<request>\n"
                + "  <getItemList>\n"
                + "      <location>\n"
                + "          <long>\n"
                + "              123\n"
                + "          </long>\n"
                + "          <lat>\n"
                + "              123\n"
                + "          </lat>\n"
                + "      </location>\n"
                + "  </getItemList>\n"
                + "</request>";
        try {
            toTest = Command.getItemList(XML.openXML(input.replaceAll("\\n", "").replaceAll(" ", "")).getFirstChild(), dummyclient);
        } catch (XML.XMLException ex) {
            Assert.fail("Coulnt read test xml");
        }

        String expeced = "";
        //toTest.toString();
    }

    /**
     * Test of getItem method, of class Command.
     */
    @Test
    public void testGetItem() {
        XML toTest = XML.createNewXML("ICH_BIN_NUR_EIN_PLATZHALTER");
        String input = "<request>\n"
                + "  <getItem>\n"
                + "      <id>1</id>\n"
                + "  </getItem>\n"
                + "</request>";
        try {
            toTest = Command.getItem(XML.openXML(input.replaceAll("\\n", "").replaceAll(" ", "")).getFirstChild(), dummyclient);
        } catch (XML.XMLException ex) {
            Assert.fail("Coulnt read test xml");
        }
        System.out.println(toTest.toString());
    }

    /**
     * Test of getItemStats method, of class Command.
     */
    @Test
    public void testGetItemStats() {
        XML toTest;
        String input = "<request>\n"
                + "  <getItemStats>\n"
                + "      <id>1</id>\n"
                + "   </getItemStats>\n"
                + "</request>";
        try {
            toTest = Command.getItemStats(XML.openXML(input.replaceAll("\\n", "").replaceAll(" ", "")).getFirstChild(), dummyclient);
        } catch (XML.XMLException ex) {
            Assert.fail("Coulnt read test xml");
        }
    }

    /**
     * Test of echo method, of class Command.
     */
    @Test
    public void testEcho() {
        XML toTest;
        String input = "<request>\n"
                + "   <echo/>\n"
                + "</request>";
        try {
            toTest = Command.echo(XML.openXML(input.replaceAll("\\n", "").replaceAll(" ", "")).getFirstChild(), dummyclient);
        } catch (XML.XMLException ex) {
            Assert.fail("Coulnt read test xml");
        }
    }

    /**
     * Test of vote method, of class Command.
     */
    @Test
    public void testVote() {
        XML toTest;
        String input = "<request>\n"
                + "  <vote>\n"
                + "      <id>1</id>\n"
                + "      <upvote>1</upvote>\n"
                + "  </vote>\n"
                + "</request>";
        try {
            toTest = Command.vote(XML.openXML(input.replaceAll("\\n", "").replaceAll(" ", "")).getFirstChild(), dummyclient);
        } catch (XML.XMLException ex) {
            Assert.fail("Coulnt read test xml");
        }
    }

    /**
     * Test of upload method, of class Command.
     */
    @Test
    public void testUpload() {
        XML toTest;
        String input = "<request>\n"
                + "  <upload>\n"
                + "      <data>123</data>\n"
                + "      <long>123</long>\n"
                + "      <lat>123</lat>\n"
                + "  </upload>\n"
                + "</request>";
        try {
            toTest = Command.upload(XML.openXML(input.replaceAll("\\n", "").replaceAll(" ", "")).getFirstChild(), dummyclient);
        } catch (XML.XMLException ex) {
            Assert.fail("Coulnt read test xml");
        }
    }

    /**
     * Test of flagComment method, of class Command.
     */
    @Test
    public void testFlagComment() {
        XML toTest;
        String input = "<request>\n"
                + "  <flagComment>\n"
                + "      <id>1</id>\n"
                + "  </flagComment>\n"
                + "</request>";
        try {
            toTest = Command.flagComment(XML.openXML(input.replaceAll("\\n", "").replaceAll(" ", "")).getFirstChild(), dummyclient);
        } catch (XML.XMLException ex) {
            Assert.fail("Coulnt read test xml");
        }
    }

    /**
     * Test of flagItem method, of class Command.
     */
    @Test
    public void testFlagItem() {
        XML toTest;
        String input = "<request>\n"
                + "  <flagItem>\n"
                + "      <id>1</id>\n"
                + "  </flagItem>\n"
                + "</request>";
        try {
            toTest = Command.flagItem(XML.openXML(input.replaceAll("\\n", "").replaceAll(" ", "")).getFirstChild(), dummyclient);
        } catch (XML.XMLException ex) {
            Assert.fail("Coulnt read test xml");
        }
    }

    /**
     * Test of getComments method, of class Command.
     */
    @Test
    public void testGetComments() {
        XML toTest;
        String input = "<request>\n"
                + "  <getComments>\n"
                + "      <id>1</id>\n"
                + "  </getComments>\n"
                + "</request>";
        try {
            toTest = Command.getComments(XML.openXML(input.replaceAll("\\n", "").replaceAll(" ", "")).getFirstChild(), dummyclient);
        } catch (XML.XMLException ex) {
            Assert.fail("Coulnt read test xml");
        }
    }

    /**
     * Test of addComment method, of class Command.
     */
    @Test
    public void testAddComment() {
        XML toTest;
        String input = "<request>\n"
                + "  <addComment>\n"
                + "      <id>1</id>\n"
                + "      <content>blarghBinEinTest</content>\n"
                + "  </addComment>\n"
                + "</request>";
        try {
            toTest = Command.addComment(XML.openXML(input.replaceAll("\\n", "").replaceAll(" ", "")).getFirstChild(), dummyclient);
        } catch (XML.XMLException ex) {
            Assert.fail("Coulnt read test xml");
        }
    }

    /**
     * Test of error method, of class Command.
     */
    @Test
    public void testError() {
        XML toTest;
        toTest = Command.error("testmessage", false);
    }

    /**
     * Test of disconnect method, of class Command.
     */
    @Test
    public void testDisconnect() {
        String input = "<request>\n"
                + "  <disconnect/>\n"
                + "</request>";
        try {
            Command.disconnect(XML.openXML(input.replaceAll("\\n", "").replaceAll(" ", "")).getFirstChild(), dummyclient);
        } catch (XML.XMLException ex) {
            Assert.fail("Coulnt read test xml");
        }
    }

}
