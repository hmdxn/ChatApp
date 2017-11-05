package com.tronography.locationchat.firebase.datamanagers;

import com.tronography.locationchat.model.Message;
import com.tronography.locationchat.model.User;

/**
 * Created by jonathancolon on 8/27/17.
 */

public interface MessageDataManagerContract {

    void getMessageLog(String roomID);

    void updateSenderName(User user);

    void saveMessage(Message message, String roomID);
}
