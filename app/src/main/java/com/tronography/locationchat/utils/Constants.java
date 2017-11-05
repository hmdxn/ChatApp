package com.tronography.locationchat.utils;

/**
 * Created by jonat on 10/10/2017.
 */

public class Constants {

    public static final String USER_MODEL = "user_model";
    public static final String MEMBERS = "members";
    public static final String MESSAGES = "messages";
    public static final String MESSAGE_MODEL = "message_model";
    public static final String CHATROOMS = "chatrooms";
    public static final String CHATROOM = "chatroom";

    private Constants(){
        //this prevents even the native class from
        //calling this constructor as well :
        throw new AssertionError();
    }
}
