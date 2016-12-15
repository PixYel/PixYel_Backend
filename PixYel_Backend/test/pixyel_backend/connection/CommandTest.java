/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import pixyel_backend.connection.socket.SocketClient;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.exceptions.UserCreationException;
import pixyel_backend.database.exceptions.UserNotFoundException;
import pixyel_backend.database.objects.User;
import pixyel_backend.xml.XML;

/**
 * Tests ONLY the structure of XML files
 *
 * @author Josua Frank
 */
public class CommandTest {

    public CommandTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        MysqlConnector.useTestDB();
        try {
            dummyclient = new SocketClient(new Socket(InetAddress.getByName("sharknoon.de"), 7331));
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

    static SocketClient dummyclient;

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

            assertEquals("setItemList", toTest.getName());
            //assertEquals("item", (toTest = toTest.getFirstChild()).getName());
            //assertEquals(6, toTest.getChildren().size());
            //List<String> list = toTest.getChildren().stream().map(xml -> xml.getName()).collect(Collectors.toList());

            //Assert.assertTrue(list.containsAll(Arrays.asList("id", "upvotes", "downvotes", "votedByUser", "rank", "date")));
        } catch (XML.XMLException ex) {
            Assert.fail("Coulnt read test xml");
        }
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

            assertEquals("setItem", toTest.getName());
            assertEquals(7, toTest.getChildren().size());
            List<String> list = toTest.getChildren().stream().map(xml -> xml.getName()).collect(Collectors.toList());

            Assert.assertTrue(list.containsAll(Arrays.asList("id", "upvotes", "downvotes", "votedByUser", "rank", "date", "data")));
        } catch (XML.XMLException ex) {
            Assert.fail("Coulnt read test xml");
        }
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

            assertEquals("setItemStats", toTest.getName());
            assertEquals(6, toTest.getChildren().size());
            List<String> list = toTest.getChildren().stream().map(xml -> xml.getName()).collect(Collectors.toList());

            Assert.assertTrue(list.containsAll(Arrays.asList("id", "upvotes", "downvotes", "votedByUser", "rank", "date")));
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

            assertEquals("echo", toTest.getName());
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

            assertEquals("voteSuccessful", toTest.getName());
            assertEquals(2, toTest.getChildren().size());
            List<String> list = toTest.getChildren().stream().map(xml -> xml.getName()).collect(Collectors.toList());

            Assert.assertTrue(list.containsAll(Arrays.asList("id", "success")));
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

            assertEquals("flagCommentSuccessful", toTest.getName());
            assertEquals(2, toTest.getChildren().size());
            List<String> list = toTest.getChildren().stream().map(xml -> xml.getName()).collect(Collectors.toList());

            Assert.assertTrue(list.containsAll(Arrays.asList("id", "success")));
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

            assertEquals("flagItemSuccessful", toTest.getName());
            assertEquals(2, toTest.getChildren().size());
            List<String> list = toTest.getChildren().stream().map(xml -> xml.getName()).collect(Collectors.toList());
            Assert.assertTrue(list.containsAll(Arrays.asList("id", "success")));
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

            assertEquals("setComments", toTest.getName());
            assertEquals("comment", (toTest = toTest.getFirstChild()).getName());
            assertEquals(3, toTest.getChildren().size());
            List<String> list = toTest.getChildren().stream().map(xml -> xml.getName()).collect(Collectors.toList());

            Assert.assertTrue(list.containsAll(Arrays.asList("id", "date", "content")));
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

            assertEquals("addCommentSuccessful", toTest.getName());
            assertEquals(2, toTest.getChildren().size());
            List<String> list = toTest.getChildren().stream().map(xml -> xml.getName()).collect(Collectors.toList());

            Assert.assertTrue(list.containsAll(Arrays.asList("id", "success")));
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

        assertEquals("error", toTest.getName());
        assertEquals(2, toTest.getChildren().size());
        List<String> list = toTest.getChildren().stream().map(xml -> xml.getName()).collect(Collectors.toList());

        Assert.assertTrue(list.containsAll(Arrays.asList("errorMessage", "isErrorFatal")));
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
