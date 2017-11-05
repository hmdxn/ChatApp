package com.tronography.locationchat.listeners;

import com.tronography.locationchat.model.ChatRoom;

import java.util.ArrayList;

/**
 * Created by jonathancolon on 8/27/17.
 */

public interface RetrieveChatRoomListener {

    void onChatRoomListRetrieved(ArrayList<ChatRoom> chatRoomList);

    void onChatRoomRetrieved(ChatRoom chatRoom);

}
