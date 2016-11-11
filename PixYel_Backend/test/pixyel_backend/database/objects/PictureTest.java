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
import pixyel_backend.database.exceptions.PictureLoadException;
import pixyel_backend.database.exceptions.PictureUploadExcpetion;

/**
 *
 * @author i01frajos445
 */
public class PictureTest {

    public Picture testpicture;
    private User user = User.addNewUser("JUnit-Test-User-" + System.currentTimeMillis());

    @BeforeClass
    public static void init() {
        MysqlConnector.useTestDB();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public PictureTest() throws DbConnectionException, Exception {
        testpicture = user.uploadPicture("testpic" + System.currentTimeMillis(), 1, 2);
    }

    /**
     * Test if upload throws an exception when picturedata = null
     */
    @Test
    public void testuploadPicture() throws PictureUploadExcpetion, PictureLoadException {

        thrown.expect(PictureUploadExcpetion.class);
        user.uploadPicture(null, 0, 0);
    }

    /**
     * Test of getPicture, of class picutre.
     */
    @Test
    public void testPictureById() throws Exception {
        int id = testpicture.getID();
        String picData = Picture.getPictureById(id).getData();
        assertEquals(picData, testpicture.getData());
    }
}
