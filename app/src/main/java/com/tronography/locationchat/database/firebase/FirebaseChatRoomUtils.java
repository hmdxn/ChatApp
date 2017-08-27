package com.tronography.locationchat.database.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tronography.locationchat.listeners.RetrieveChatRoomListener;
import com.tronography.locationchat.lobby.LobbyContract;
import com.tronography.locationchat.model.ChatRoomModel;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.tronography.locationchat.database.firebase.FirebaseDatabaseReference.getChatRoomReference;
import static com.tronography.locationchat.database.firebase.FirebaseDatabaseReference.getMessageReference;
import static com.tronography.locationchat.database.firebase.FirebaseDatabaseReference.getRoot;
import static com.tronography.locationchat.utils.ObjectUtils.isNull;


public class FirebaseChatRoomUtils {

    
    public void queryChatRoomByID(final String chatRoomId, final RetrieveChatRoomListener retrieveChatRoomListener) {
        getRoot().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatRoomModel chatRoomModel = dataSnapshot
                        .child("chatrooms")
                        .child(chatRoomId)
                        .child("chatroom")
                        .getValue(ChatRoomModel.class);
                if (!isNull(chatRoomModel)) {
                    retrieveChatRoomListener.onChatRoomRetrieved(chatRoomModel);
                } else {
                    Log.e(TAG, "onDataChange: " + " that key does not exist");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.toString());
            }
        });
    }

    public void addChatRoomEventListener(final LobbyContract.UserActionListener presenter) {
        getChatRoomReference().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                presenter.childAdded(dataSnapshot, s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @NonNull
    private HashMap<String, Object> setDatabaseChatRoomValues(ChatRoomModel chatRoomObject) {
        HashMap<String, Object> chatRoomModelMap = new HashMap<>();
        chatRoomModelMap.put("chatroom", chatRoomObject);
        return chatRoomModelMap;
    }

    @NonNull
    private HashMap<String, Object> setDatabaseConversationValues(ChatRoomModel chatRoomObject) {
        HashMap<String, Object> chatRoomModelMap = new HashMap<>();
        chatRoomModelMap.put("conversation", chatRoomObject);
        return chatRoomModelMap;
    }

    public void addChatRoomToFirebase(ChatRoomModel chatRoomModel) {
        //creates a unique key identifier
        HashMap<String, Object> uniqueMemberIdentifier = new HashMap<>();

        //appends root with unique key
        getChatRoomReference().updateChildren(uniqueMemberIdentifier);

        //reference the unique key object in the database
        DatabaseReference chatRoomRoot = getChatRoomReference().child(chatRoomModel.getId());

        //now we must generate the children of this new object
        HashMap<String, Object> chatRoomModelMap = setDatabaseChatRoomValues(chatRoomModel);

        //confirm changes
        chatRoomRoot.updateChildren(chatRoomModelMap);
    }

    public void addNewConversationToMessages(ChatRoomModel chatRoomModel) {
        //creates a unique key identifier
        HashMap<String, Object> uniqueMemberIdentifier = new HashMap<>();
        uniqueMemberIdentifier.put(chatRoomModel.getId(), chatRoomModel.getName());

        //appends root with unique key
        getMessageReference().updateChildren(uniqueMemberIdentifier);

//        //reference the unique key object in the database
//        DatabaseReference messageRoot = messageReference.child(chatRoomModel.getId());

    }

    public void retrieveChatRoomsFromFirebase(final RetrieveChatRoomListener listener) {
        final ArrayList<ChatRoomModel> chatRoomList = new ArrayList<>();
        getChatRoomReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    String key = child.getKey();
                    ChatRoomModel chatroomModel = dataSnapshot
                            .child(key)
                            .child("chatroom")
                            .getValue(ChatRoomModel.class);
                    Log.e(TAG, "getMessageLog: " + chatroomModel.getName());
                    chatRoomList.add(chatroomModel);
                }
                listener.onChatRoomListRetrieved(chatRoomList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
