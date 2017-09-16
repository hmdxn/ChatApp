package com.tronography.locationchat.model;

import static com.tronography.locationchat.utils.DateUtils.dateFormatter;


public class MessageModel {

    private String messageId;
    private String message;
    private String timeStamp;
    private String senderId;
    private String senderName;

    public MessageModel() {
    }

    public MessageModel(String messageId, String message, String timeStamp, String senderId, String senderName) {
        this.messageId = messageId;
        this.message = message;
        this.timeStamp = timeStamp;
        this.senderId = senderId;
        this.senderName = senderName;
    }

    //this constructor should only be used when retrieving message data from firebase
    // TODO: make private and use another method that utilizes this constructor to prevent id manipulation
    public MessageModel(String senderId, String senderName, String message, String timeStamp) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public MessageModel(String message) {
        this.message = message;
        this.timeStamp = String.valueOf(dateFormatter(getCurrentTime()));
    }

    public MessageModel(String message, String senderId, String senderName) {
        this.message = message;
        this.senderId = senderId;
        this.senderName = senderName;
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

    public String getSenderName() {
        return senderName;
    }

    public void setMessageId(String messageId){
        this.messageId = messageId;
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
                ", senderName=" + senderName +
                '}';
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}


