/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database;

import pixyel_backend.database.exceptions.CommentCreationException;
import pixyel_backend.database.objects.Comment;

/**
 *
 * @author Yannick
 */
public class BackendFunctions {

    private final DbConnection con;
    private final int userId;
    public BackendFunctions(DbConnection con, int userid){
        this.con = con;
        this.userId=userid;
    }
    
    public Comment getComment(int commentId) throws CommentCreationException{
        return new Comment(commentId,this.con);
    }
    
    public void newComment (int pictureID,String comment){
        System.out.println("test354");
        Comment.newComment(pictureID, this.userId, comment, this.con);
        System.out.println("test355");
    }

}
