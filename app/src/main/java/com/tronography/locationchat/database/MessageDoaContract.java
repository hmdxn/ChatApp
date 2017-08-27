package com.tronography.locationchat.database;

import com.tronography.locationchat.listeners.RetrieveMessageLogListener;
import com.tronography.locationchat.model.MessageModel;
import com.tronography.locationchat.model.UserModel;

/**
 * Created by jonathancolon on 8/27/17.
 */

public interface MessageDoaContract {

    void getMessageLog(final RetrieveMessageLogListener listener, String roomID);

    void updateSenderName(UserModel userModel);

    void saveMessage(MessageModel messageModel, String roomID);
}
