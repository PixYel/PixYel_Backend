/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pixyel_backend.database.Columns;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.exceptions.FlagFailedExcpetion;
import static org.junit.Assert.assertEquals;
import pixyel_backend.database.exceptions.CommentCreationException;
import pixyel_backend.database.exceptions.PictureLoadException;
import pixyel_backend.database.exceptions.PictureUploadExcpetion;
import pixyel_backend.database.exceptions.UserCreationException;

/**
 *
 * @author Da_Groove
 */
public class CommentTest {

    private final User user;
    private static final String COMMENT_TEXT = System.currentTimeMillis() + "";
    int pictureId;
    Picture picture;

    @BeforeClass
    public static void init() {
        MysqlConnector.useTestDB();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public CommentTest() throws UserCreationException, PictureUploadExcpetion, PictureLoadException {
        this.user = User.addNewUser("JUnit-Test-User-" + System.currentTimeMillis());
        this.pictureId = user.uploadPicture("tollesBild", new Coordinate(1.234, 4.321));
        this.picture = user.getPicture(pictureId);
    }

    @Test
    public void testUploadedComment() throws SQLException, CommentCreationException {
        user.addNewComment(COMMENT_TEXT + 1, picture.getId());
        List<Comment> commentList = Comment.getCommentsForPicutre(picture.getId());
        Comment addedComment = commentList.get(commentList.size() - 1);
        assertEquals(COMMENT_TEXT + 1, addedComment.getComment());
        assertEquals(user.getID(), addedComment.getUserId());
        assertEquals(picture.getId(), addedComment.getPictureId());
    }

    @Test
    public void testflagComment() throws SQLException, FlagFailedExcpetion, CommentCreationException {
        user.addNewComment(COMMENT_TEXT + 2, picture.getId());
        List<Comment> CommentList = Comment.getCommentsForPicutre(picture.getId());
        Comment addedComment = CommentList.get(CommentList.size() - 1);
        Comment.flagComment(addedComment.getUserId(), addedComment.getCommentId());
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("SELECT * FROM commentflags WHERE " + Columns.COMMENT_ID + " = ?")) {
            statement.setInt(1, addedComment.getCommentId());
            ResultSet newResult = statement.executeQuery();

            newResult.next();
            assertEquals(addedComment.getCommentId(), newResult.getInt(Columns.COMMENT_ID));
        }
    }
}
