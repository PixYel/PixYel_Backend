/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database.objects;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.exceptions.DbConnectionException;
import pixyel_backend.database.exceptions.UserCreationException;

/**
 *
 * @author Yannick
 */
public class WebUserTest {

    public static final String TIME = String.valueOf(System.currentTimeMillis());
    public static final String TESTUSERNAME = "test-" +TIME;
    public static final String TESTUSERPASSWORD = TIME;
    public static final String ANOTHERUSER = "anotheruser";

    @BeforeClass
    public static void init() throws UserCreationException {
        MysqlConnector.useTestDB();
        WebUser.addNewWebUser(TESTUSERNAME, TESTUSERPASSWORD);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public WebUserTest() throws DbConnectionException, Exception {
    }

    /**
     * Test of addNewUser method, adding a dublicate user
     *
     * @throws pixyel_backend.database.exceptions.UserCreationException
     */
    @Test
    public void testAddDublicateUserName() throws UserCreationException {
        thrown.expect(UserCreationException.class);
        WebUser.addNewWebUser(TESTUSERNAME, TESTUSERPASSWORD);
    }

    /**
     * Test of addNewUser method, using a to short username
     *
     * @throws pixyel_backend.database.exceptions.UserCreationException
     */
    @Test
    public void testToShortUserName() throws UserCreationException {
        thrown.expect(UserCreationException.class);
        WebUser.addNewWebUser("0", TESTUSERPASSWORD);
    }

    /**
     * Test of addNewUser method, using a to long username
     *
     * @throws pixyel_backend.database.exceptions.UserCreationException
     */
    @Test
    public void testToLongUserName() throws UserCreationException {
        thrown.expect(UserCreationException.class);
        WebUser.addNewWebUser("000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", TESTUSERPASSWORD);
    }

    /**
     * Test of addNewUser method, using no username
     *
     * @throws pixyel_backend.database.exceptions.UserCreationException
     */
    @Test
    public void testNoUserName() throws UserCreationException {
        thrown.expect(UserCreationException.class);
        WebUser.addNewWebUser(null, TESTUSERPASSWORD);
    }

    /**
     * Test of addNewUser method, using a to short password
     *
     * @throws pixyel_backend.database.exceptions.UserCreationException
     */
    @Test
    public void testToShortPassword() throws UserCreationException {
        thrown.expect(UserCreationException.class);
        WebUser.addNewWebUser(ANOTHERUSER, "1");
    }

    /**
     * Test of addNewUser method, using a to long password
     *
     * @throws pixyel_backend.database.exceptions.UserCreationException
     */
    @Test
    public void testToLongPassword() throws UserCreationException {
        thrown.expect(UserCreationException.class);
        WebUser.addNewWebUser(ANOTHERUSER, "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
    }

    /**
     * Test of addNewUser method, using no password
     *
     * @throws pixyel_backend.database.exceptions.UserCreationException
     */
    @Test
    public void testNoPassword() throws UserCreationException {
        thrown.expect(UserCreationException.class);
        WebUser.addNewWebUser(ANOTHERUSER, null);
    }

    /**
     * Test of login method.
     */
    @Test
    public void login() {
        assertTrue(WebUser.loginWebUser(TESTUSERNAME, TESTUSERPASSWORD));

        assertFalse(WebUser.loginWebUser("everything", "went wrong"));
        assertFalse(WebUser.loginWebUser(TESTUSERNAME, "wrongpassword"));
        assertFalse(WebUser.loginWebUser("wronguser", TESTUSERPASSWORD));
    }
}
