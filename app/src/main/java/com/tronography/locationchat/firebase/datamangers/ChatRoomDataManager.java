package com.tronography.locationchat.firebase.datamangers;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tronography.locationchat.firebase.utils.FirebaseDatabaseReference;
import com.tronography.locationchat.listeners.RetrieveChatRoomListener;
import com.tronography.locationchat.model.Chatroom;

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
                Chatroom chatroom = dataSnapshot
                        .child(CHATROOMS)
                        .child(postalCode)
                        .child("chatroom")
                        .getValue(Chatroom.class);

                if (!isNull(chatroom)) {
                    retrieveChatRoomListener.onChatRoomRetrieved(chatroom);
                } else {
                    addToFirebase(new Chatroom(postalCode));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.toString());
            }
        });
    }

    public void addToFirebase(Chatroom chatroom) {
        HashMap<String, Object> uniqueMemberIdentifier = new HashMap<>();
        FirebaseDatabaseReference.getChatRoomReference().updateChildren(uniqueMemberIdentifier);

        DatabaseReference chatRoomRoot = FirebaseDatabaseReference.getChatRoomReference().child(chatroom.getName());

        HashMap<String, Object> chatRoomMap = new HashMap<>();
        chatRoomMap.put(CHATROOM, chatroom);

        //confirm changes
        chatRoomRoot.updateChildren(chatRoomMap);
    }
}
