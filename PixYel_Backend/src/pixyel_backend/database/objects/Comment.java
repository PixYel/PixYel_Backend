/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database.objects;

import java.util.Date;

/**
 *
 * @author Groove
 */
public class Comment {
    private final int commentId;
    private int pictureId;
    private int userId;
    private String comment;
    private final Date commentDate;
    private int flags;
    
    public Comment(int commentId, int pictureId, int userId, String comment, Date commentDate, int flags){
        this.commentId = commentId;
        this.pictureId = pictureId;
        this.userId = userId;
        this.comment = comment;
        this.commentDate = commentDate;
        this.flags = flags;
    }

    /**
     * @return the commentId
     */
    public int getCommentId() {
        return commentId;
    }

    /**
     * @return the pictureId
     */
    public int getPictureId() {
        return pictureId;
    }

    /**
     * @param pictureId the pictureId to set
     */
    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the commentDate
     */
    public Date getCommentDate() {
        return commentDate;
    }

    /**
     * @return the flags
     */
    public int getFlags() {
        return flags;
    }
    
    public void addFlag(){
        this.flags++;
    }
}
