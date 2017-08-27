package com.tronography.locationchat.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tronography.locationchat.model.ChatRoomModel;
import com.tronography.locationchat.model.MessageModel;
import com.tronography.locationchat.model.UserModel;

import java.util.HashMap;
import java.util.Objects;

import static android.content.ContentValues.TAG;
import static com.tronography.locationchat.firebase.FirebaseDatabaseReference.getChatRoomReference;
import static com.tronography.locationchat.firebase.FirebaseDatabaseReference.getMessageReference;

/**
 * Created by jonathancolon on 8/26/17.
 */

public class UpdateMessageSenderName {

    private UserModel userModel;

    public UpdateMessageSenderName(UserModel userModel) {
        this.userModel = userModel;
    }

    public void updateSenderName() {
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
                    Log.e(TAG, "retrieveMessagesFromFirebase: " + chatroomModel.getName());
                    updateMessageSenderName(userModel, chatroomModel.getId());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    private void updateMessageSenderName(final UserModel userModel, final String roomID) {
        getMessagesByChatRoomID(roomID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {

                    MessageModel messageModel = accessMessageModelInFirebase(dataSnapshot, child);

                    if (Objects.equals(messageModel.getSenderId(), userModel.getId())) {
                        applySenderNameChangeInFirebase(roomID, messageModel, userModel.getUsername());
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
        return getMessageReference().child(roomID);
    }

    private MessageModel accessMessageModelInFirebase(DataSnapshot dataSnapshot, DataSnapshot child) {
        String key = child.getKey();
        return dataSnapshot
                .child(key)
                .child("message_model")
                .getValue(MessageModel.class);
    }

    private void applySenderNameChangeInFirebase(String roomID, MessageModel messagModel, String newUserName) {
        //reference the unique key object in the database
        DatabaseReference messageRoot = getMessagesByChatRoomID(roomID).child(messagModel.getMessageId());
        messagModel.setUsername(newUserName);
        //now we must generate the children of this new object
        HashMap<String, Object> userModelMap = setDatabaseMessageValues(messagModel);
        //confirm changes
        messageRoot.updateChildren(userModelMap);
    }

    @NonNull
    private HashMap<String, Object> setDatabaseMessageValues(MessageModel messageModel) {
        HashMap<String, Object> messageModelMap = new HashMap<>();
        messageModelMap.put("message_model", messageModel);
        return messageModelMap;
    }
}
