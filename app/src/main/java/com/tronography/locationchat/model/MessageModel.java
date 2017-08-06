package com.tronography.locationchat.model;

import java.util.UUID;

import static com.tronography.locationchat.utils.DateUtils.dateFormatter;

/**
 * Created by jonathancolon on 7/31/17.
 */

public class MessageModel {

    private String messageId;
    private String message;
    private String timeStamp;
    private String senderId;
    private String username;

    public MessageModel() {
    }

    //this constructor should only be used when retrieving message data from firebase
    // TODO: make private and use another method that utilizes this constructor to prevent id manipulation
    public MessageModel(String senderId, String username, String message, String timeStamp) {
        this.messageId = UUID.randomUUID().toString();
        this.senderId = senderId;
        this.username = username;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public MessageModel(String message) {
        this.messageId = UUID.randomUUID().toString();
        this.message = message;
        this.timeStamp = String.valueOf(dateFormatter(getCurrentTime()));
    }

    public MessageModel(String message, String senderId, String username) {
        this.messageId = UUID.randomUUID().toString();
        this.message = message;
        this.senderId = senderId;
        this.username = username;
        this.timeStamp = String.valueOf(dateFormatter(getCurrentTime()));
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getUsername() {
        return username;
    }

    public String getMessageId() {
        return messageId;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                ", timeStamp='" + timeStamp + '\'' +
                ", message='" + message + '\'' +
                ", senderId='" + senderId + '\'' +
                ", username=" + username +
                '}';
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }
}

