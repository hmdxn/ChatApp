package com.tronography.locationchat.firebase.datamanagers;

import com.tronography.locationchat.model.ChatRoom;

/**
 * Created by jonathancolon on 8/27/17.
 */

public interface ChatRoomDataManagerContract {

    void queryByRoomId(String chatRoomId);

    void addChatroomToFirebase(ChatRoom chatRoom);

    void retrieveFromFirebase();
}
