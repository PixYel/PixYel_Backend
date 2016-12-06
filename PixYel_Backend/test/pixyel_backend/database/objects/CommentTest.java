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
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pixyel_backend.database.Columns;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.exceptions.DbConnectionException;
import pixyel_backend.database.exceptions.FlagFailedExcpetion;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Da_Groove
 */
public class CommentTest {

    private final User user = User.addNewUser("JUnit-Test-User-" + System.currentTimeMillis());
    private static String commentText = System.currentTimeMillis() + "";
    Picture picture = user.uploadPicture("tollesBild", new Coordinate(1234, 4321));

    @BeforeClass
    public static void init() {
        MysqlConnector.useTestDB();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public CommentTest() throws DbConnectionException, Exception {
        user.addNewComment(commentText, picture.getId());
    }

    @Test
    public void testUploadedComment() throws SQLException {
        List<Comment> CommentList =  Comment.getCommentsForPicutre(picture.getId());
        Comment addedComment = CommentList.get(CommentList.size()-1);
        assertEquals(commentText, addedComment.getComment());
        assertEquals(user.getID(), addedComment.getUserId());
        assertEquals(picture.getId(), addedComment.getPictureId());
    }

    @Test
    public void testflagComment() throws SQLException, FlagFailedExcpetion {
        List<Comment> CommentList =  Comment.getCommentsForPicutre(picture.getId());
        Comment addedComment = CommentList.get(CommentList.size()-1);
        Comment.flagComment(addedComment.getUserId(),addedComment.getCommentId());
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("SELECT * FROM commentflags WHERE " + Columns.COMMENT_ID + " = ?")) {
            statement.setInt(1, addedComment.getCommentId());
            ResultSet newResult = statement.executeQuery();
            
            
            newResult.next();
            assertEquals(addedComment.getCommentId(), newResult.getInt(Columns.COMMENT_ID));
        }
    }
}
