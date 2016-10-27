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
    public BackendFunctions(DbConnection con){
        this.con = con;
    }
    
    public Comment getComment(int commentId) throws CommentCreationException{
        return new Comment(commentId,this.con);
    }
    
    public void newComment (int pictureID,int userId, String comment){
        Comment.newComment(pictureID, userId, comment, this.con);
    }

}
