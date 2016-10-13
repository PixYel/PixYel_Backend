/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database.objects;

import java.util.Date;

/**
 *
 * @author Da_Groove
 */
public class PictureInfo {
    private int id;
    private double xCoord;
    private double yCoord;
    private Date timestamp;
    private int upvotes;
    private int downvotes;
    private int flags;
    private int userId;
    private String commentsId;
    
    public PictureInfo(int id, double xCoord, double yCoord, Date timestamp, int upvotes, int downvotes, int flags, int userId, String commentsId){
        this.id = id;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.timestamp = timestamp;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.flags = flags;
        this.userId = userId;
        this.commentsId = commentsId;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the xCoord
     */
    public double getxCoord() {
        return xCoord;
    }

    /**
     * @param xCoord the xCoord to set
     */
    public void setxCoord(double xCoord) {
        this.xCoord = xCoord;
    }

    /**
     * @return the yCoord
     */
    public double getyCoord() {
        return yCoord;
    }

    /**
     * @param yCoord the yCoord to set
     */
    public void setyCoord(double yCoord) {
        this.yCoord = yCoord;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the upvotes
     */
    public int getUpvotes() {
        return upvotes;
    }

    /**
     * @param upvotes the upvotes to set
     */
    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    /**
     * @return the downvotes
     */
    public int getDownvotes() {
        return downvotes;
    }

    /**
     * @param downvotes the downvotes to set
     */
    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    /**
     * @return the flags
     */
    public int getFlags() {
        return flags;
    }

    /**
     * @param flags the flags to set
     */
    public void setFlags(int flags) {
        this.flags = flags;
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
     * @return the commentsId
     */
    public String getCommentsId() {
        return commentsId;
    }

    /**
     * @param commentsId the commentsId to set
     */
    public void setCommentsId(String commentsId) {
        this.commentsId = commentsId;
    }
}
