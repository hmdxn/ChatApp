package com.tronography.locationchat.firebase.datamanagers;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tronography.locationchat.listeners.RetrieveChatRoomListener;
import com.tronography.locationchat.model.ChatRoom;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.tronography.locationchat.utils.Constants.CHATROOM;
import static com.tronography.locationchat.utils.Constants.CHATROOMS;
import static com.tronography.locationchat.utils.ObjectUtils.isNull;


public class ChatRoomDataManager implements ChatRoomDataManagerContract {


    private RetrieveChatRoomListener retrieveChatRoomListener;

    public ChatRoomDataManager(RetrieveChatRoomListener retrieveChatRoomListener) {
        this.retrieveChatRoomListener = retrieveChatRoomListener;
    }

    @Override
    public void queryByRoomId(final String chatRoomId) {
        FirebaseDatabaseReference.getRoot().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatRoom chatRoom = dataSnapshot
                        .child(CHATROOMS)
                        .child(chatRoomId)
                        .child(CHATROOMS)
                        .getValue(ChatRoom.class);

                if (!isNull(chatRoom)) {
                    retrieveChatRoomListener.onChatRoomRetrieved(chatRoom);
                } else {
                    Log.e(TAG, "onDataChange: " + " that chat room does not exist");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.toString());
            }
        });
    }

    @Override
    public void addChatroomToFirebase(ChatRoom chatRoom) {
        HashMap<String, Object> uniqueMemberIdentifier = new HashMap<>();
        FirebaseDatabaseReference.getChatRoomReference().updateChildren(uniqueMemberIdentifier);

        DatabaseReference chatRoomRoot = FirebaseDatabaseReference.getChatRoomReference().child(chatRoom.getId());

        HashMap<String, Object> chatRoomMap = new HashMap<>();
        chatRoomMap.put(CHATROOM, chatRoom);

        //confirm changes
        chatRoomRoot.updateChildren(chatRoomMap);
    }

    @Override
    public void retrieveFromFirebase() {
        final ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
        FirebaseDatabaseReference.getChatRoomReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    String key = child.getKey();
                    ChatRoom chatroom = dataSnapshot
                            .child(key)
                            .child(CHATROOM)
                            .getValue(ChatRoom.class);
                    Log.e(TAG, "getMessageLog: " + chatroom.getName());
                    chatRoomList.add(chatroom);
                }
                retrieveChatRoomListener.onChatRoomListRetrieved(chatRoomList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
