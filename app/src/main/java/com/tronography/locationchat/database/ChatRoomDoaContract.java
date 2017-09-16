package com.tronography.locationchat.database;

import com.tronography.locationchat.listeners.RetrieveChatRoomListener;
import com.tronography.locationchat.model.ChatRoomModel;

/**
 * Created by jonathancolon on 8/27/17.
 */

public interface ChatRoomDoaContract {
    void queryChatRoomByID(String chatRoomId, RetrieveChatRoomListener retrieveChatRoomListener);

    void addChatRoomToFirebase(ChatRoomModel chatRoomModel);

    void addNewConversationToMessages(ChatRoomModel chatRoomModel);

    void retrieveChatRoomsFromFirebase(RetrieveChatRoomListener listener);
}
