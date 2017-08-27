package com.tronography.locationchat.listeners;

import com.tronography.locationchat.model.MessageModel;

import java.util.ArrayList;

/**
 * Created by jonathancolon on 8/27/17.
 */

public interface RetrieveMessageLogListener {

    void onMessageLogReceived(ArrayList<MessageModel> messageLog);

}
