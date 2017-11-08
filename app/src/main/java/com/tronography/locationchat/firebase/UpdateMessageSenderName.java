package com.tronography.locationchat.firebase;

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

import static android.content.ContentValues.TAG;
import static com.tronography.locationchat.utils.Constants.CHATROOM;


public class UpdateMessageSenderName {

    private User user;


    public UpdateMessageSenderName(User user) {
        this.user = user;
    }

    public void updateSenderName() {
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
                    updateMessageSenderName(user, chatroom.getId());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.toString());
            }

        });
    }

    private void updateMessageSenderName(final User user, final String roomID) {
        FirebaseDatabaseReference.getChatRoomMessageRef(roomID).addListenerForSingleValueEvent(new ValueEventListener() {

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
                        applySenderNameChangeInFirebase(roomID, message, user.getUsername());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.toString());
            }
        });
    }

    private DatabaseReference getMessagesByChatRoomID(String roomID) {
        return FirebaseDatabaseReference.getMessageReference().child(roomID);
    }

    private void applySenderNameChangeInFirebase(String roomID, Message messagModel, String newUserName) {
        //reference the unique key object in the database
        DatabaseReference messageRoot = getMessagesByChatRoomID(roomID).child(messagModel.getMessageId());
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
