/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pixyel_backend.database.Columns;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.SqlUtils;
import pixyel_backend.database.exceptions.CommentNotFoundException;
import pixyel_backend.database.exceptions.DbConnectionException;
import pixyel_backend.database.exceptions.FlagFailedExcpetion;

/**
 *
 * @author Da_Groove
 */
public class CommentTest {

    private final User user = User.addNewUser("JUnit-Test-User-" + System.currentTimeMillis());
    private static String commentText = System.currentTimeMillis() + "";
    Picture returnpicture = user.uploadPicture("tollesBild", 1234, 4321);

    @BeforeClass
    public static void init() {
        MysqlConnector.useTestDB();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public CommentTest() throws DbConnectionException, Exception {
        user.addNewComment(commentText, returnpicture.getId());
    }

    @Test
    public void testUploadedComment() throws SQLException {
        ResultSet result;
        PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("SELECT * FROM comments WHERE " + Columns.TEXT + " = ?");
        commentText = SqlUtils.escapeString(commentText);
        statement.setString(1, commentText);
        result = statement.executeQuery();

        if (result == null || !result.isBeforeFirst()) {
            throw new CommentNotFoundException();
        }
        result.next();
        assertEquals(commentText, result.getString(Columns.TEXT));
        assertEquals(user.getID(), result.getInt(Columns.USER_ID));
        assertEquals(returnpicture.getId(), result.getInt(Columns.PICTURE_ID));
        statement.close();
    }

    @Test
    public void testflagComment() throws SQLException, FlagFailedExcpetion {
        ResultSet result;
        PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("SELECT * FROM comments WHERE " + Columns.TEXT + " = ?");
        commentText = SqlUtils.escapeString(commentText);
        statement.setString(1, commentText);
        result = statement.executeQuery();
        result.next();
        int commentid = result.getInt(Columns.ID);
        user.flagComment(commentid);
        statement = MysqlConnector.getConnection().prepareStatement("SELECT * FROM commentflags WHERE " + Columns.COMMENT_ID + " = ?");
        statement.setInt(1, commentid);
        ResultSet newResult = statement.executeQuery();
        

        newResult.next();
        assertEquals(commentid, newResult.getInt(Columns.COMMENT_ID));
        statement.close();
    }
}
