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
import pixyel_backend.database.exceptions.VoteFailedException;

/**
 *
 * @author i01frajos445
 */
public class PictureTest {

    public Picture testpicture;
    private final User user = User.addNewUser("JUnit-Test-User-" + System.currentTimeMillis());

    @BeforeClass
    public static void init() {
        MysqlConnector.useTestDB();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public PictureTest() throws DbConnectionException, Exception {
        int testpictureId = user.uploadPicture("testpic" + System.currentTimeMillis(), new Coordinate(1, 2));
        testpicture = user.getPicture(testpictureId);
    }

    /**
     * Test if upload throws an exception when picturedata = null
     *
     * @throws pixyel_backend.database.exceptions.PictureUploadExcpetion
     * @throws pixyel_backend.database.exceptions.PictureLoadException
     */
    @Test
    public void testuploadPicture() throws PictureUploadExcpetion, PictureLoadException {

        thrown.expect(PictureUploadExcpetion.class);
        user.uploadPicture(null, new Coordinate(0, 0));
    }

    /**
     * tests the vote-mechanics
     *
     * @throws pixyel_backend.database.exceptions.PictureUploadExcpetion
     * @throws pixyel_backend.database.exceptions.PictureLoadException
     * @throws pixyel_backend.database.exceptions.VoteFailedException
     */
    @Test
    public void testvotes() throws PictureUploadExcpetion, PictureLoadException, VoteFailedException {
        int picId = user.uploadPicture("test", new Coordinate(0, 0));
        Picture pic = user.getPicture(picId);
        assertEquals(0, pic.getVoteStatus());
        assertEquals(0, pic.getDownvotes());
        assertEquals(0, pic.getUpvotes());
        user.downvotePicture(pic.getId());
        pic = user.getPicture(pic.getId());
        assertEquals(-1, pic.getVoteStatus());
        assertEquals(1, pic.getDownvotes());
    }

    /**
     * Test of getPicture, of class picutre.
     * @throws java.lang.Exception
     */
    @Test
    public void testPictureById() throws Exception {
        int id = testpicture.getId();
        String picData = Picture.getPictureById(id, user.getID()).getData();
        assertEquals(picData, testpicture.getData());
    }
}