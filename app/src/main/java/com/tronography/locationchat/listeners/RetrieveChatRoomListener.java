package com.tronography.locationchat.listeners;

import com.tronography.locationchat.model.ChatRoomModel;

import java.util.ArrayList;

/**
 * Created by jonathancolon on 8/27/17.
 */

public interface RetrieveChatRoomListener {

    void onChatRoomListRetrieved(ArrayList<ChatRoomModel> chatRoomList);

    void onChatRoomRetrieved(ChatRoomModel chatRoomModel);
}
