package com.tronography.locationchat.firebase.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tronography.locationchat.model.Chatroom;
import com.tronography.locationchat.model.Message;
import com.tronography.locationchat.model.User;

import java.util.HashMap;
import java.util.Objects;

import static com.tronography.locationchat.utils.Constants.Firebase.CHATROOM;


public class UpdateMessageSenderName {

    private User user;
    private static final String TAG = UpdateMessageSenderName.class.getSimpleName();


    public UpdateMessageSenderName(User user) {
        this.user = user;
    }

    public void applyChanges() {
        FirebaseDatabaseReference.getChatRoomReference().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    String key = child.getKey();
                    Chatroom chatroom = dataSnapshot
                            .child(key)
                            .child(CHATROOM)
                            .getValue(Chatroom.class);
                    Log.e(TAG, "getMessageLog: " + chatroom.getName());
                    findAndUpdatePreviouslySentMessages(user, chatroom.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.toString());
            }
        });
    }

    private void findAndUpdatePreviouslySentMessages(final User user, final String roomName) {
        FirebaseDatabaseReference.getChatRoomMessageRef(roomName).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {

                    String key = child.getKey();
                    Message message = dataSnapshot
                            .child(key)
                            .child("message_model")
                            .getValue(Message.class);

                    if (Objects.equals(message.getSenderId(), user.getId())) {
                        updateSenderName(roomName, message, user.getUsername());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.toString());
            }
        });
    }

    private DatabaseReference getMessagesByChatroomName(String roomName) {
        return FirebaseDatabaseReference.getMessageReference().child(roomName);
    }

    private void updateSenderName(String roomName, Message messagModel, String newUserName) {
        //reference the unique key object in the database
        DatabaseReference messageRoot = getMessagesByChatroomName(roomName).child(messagModel.getMessageId());
        messagModel.setSenderName(newUserName);
        //now we must generate the children of this new object
        HashMap<String, Object> userModelMap = setDatabaseMessageValues(messagModel);
        //confirm changes
        messageRoot.updateChildren(userModelMap);
    }

    @NonNull
    private HashMap<String, Object> setDatabaseMessageValues(Message message) {
        HashMap<String, Object> messageModelMap = new HashMap<>();
        messageModelMap.put("message_model", message);
        return messageModelMap;
    }
}
