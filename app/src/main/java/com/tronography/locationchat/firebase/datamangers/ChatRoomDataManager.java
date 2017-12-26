package com.tronography.locationchat.firebase.datamangers;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tronography.locationchat.firebase.utils.FirebaseDatabaseReference;
import com.tronography.locationchat.listeners.RetrieveChatRoomListener;
import com.tronography.locationchat.model.ChatRoom;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

import static com.tronography.locationchat.utils.Constants.Firebase.CHATROOM;
import static com.tronography.locationchat.utils.Constants.Firebase.CHATROOMS;
import static com.tronography.locationchat.utils.ObjectUtils.isNull;


public class ChatRoomDataManager {


    private RetrieveChatRoomListener retrieveChatRoomListener;

    public ChatRoomDataManager(RetrieveChatRoomListener retrieveChatRoomListener) {
        this.retrieveChatRoomListener = retrieveChatRoomListener;
    }

    public void retrieveChatroomByPostalCode(final String postalCode) {
        FirebaseDatabaseReference.getRoot().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatRoom chatRoom = dataSnapshot
                        .child(CHATROOMS)
                        .child(postalCode)
                        .child("chatroom")
                        .getValue(ChatRoom.class);

                if (!isNull(chatRoom)) {
                    retrieveChatRoomListener.onChatRoomRetrieved(chatRoom);
                } else {
                    addToFirebase(new ChatRoom(postalCode));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.toString());
            }
        });
    }

    public void addToFirebase(ChatRoom chatRoom) {
        HashMap<String, Object> uniqueMemberIdentifier = new HashMap<>();
        FirebaseDatabaseReference.getChatRoomReference().updateChildren(uniqueMemberIdentifier);

        DatabaseReference chatRoomRoot = FirebaseDatabaseReference.getChatRoomReference().child(chatRoom.getName());

        HashMap<String, Object> chatRoomMap = new HashMap<>();
        chatRoomMap.put(CHATROOM, chatRoom);

        //confirm changes
        chatRoomRoot.updateChildren(chatRoomMap);
    }
}
