package com.tronography.locationchat.database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tronography.locationchat.database.firebase.UpdateMessageSenderName;
import com.tronography.locationchat.listeners.RetrieveMessageLogListener;
import com.tronography.locationchat.model.MessageModel;
import com.tronography.locationchat.model.UserModel;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.tronography.locationchat.database.firebase.FirebaseDatabaseReference.getChatRoomMessageRef;
import static com.tronography.locationchat.database.firebase.FirebaseDatabaseReference.getMessageReference;
import static com.tronography.locationchat.utils.ObjectUtils.isNull;


public class MessageDoa implements MessageDoaContract {

    @Override
    public void saveMessage(MessageModel messageModel, String roomID) {
        //creates a unique key identifier
        HashMap<String, Object> uniqueMessageIdentifier = new HashMap<>();

        //appends root with unique key
        getChatRoomMessageRef(roomID).updateChildren(uniqueMessageIdentifier);

        //set push key to message ID
        String messageId = getMessageReference().push().getKey();
        messageModel.setMessageId(messageId);

        //references the object using the message ID in the database
        DatabaseReference messageRoot = getChatRoomMessageRef(roomID).child(messageId);

        //assigns values to the children of this new message object
        HashMap<String, Object> messageValueMap = setDatabaseMessageValues(messageModel);

        //confirm changes
        messageRoot.updateChildren(messageValueMap);
    }

    @NonNull
    private HashMap<String, Object> setDatabaseMessageValues(MessageModel messageModel) {
        HashMap<String, Object> messageModelMap = new HashMap<>();
        messageModelMap.put("message_model", messageModel);
        return messageModelMap;
    }

    @Override
    public void updateSenderName(UserModel userModel) {
        UpdateMessageSenderName updateMessageSenderName = new UpdateMessageSenderName(userModel);
        updateMessageSenderName.updateSenderName();
    }

    @Override
    public void getMessageLog(final RetrieveMessageLogListener listener, String roomID) {
        final ArrayList<MessageModel> refreshedMessageLog = new ArrayList<>();
        getChatRoomMessageRef(roomID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {

                    MessageModel messageModel = accessMessageModel(dataSnapshot, child);

                    if (!isNull(messageModel)) {
                        Log.e(TAG, "getMessageLog: " + messageModel.getMessage());
                        refreshedMessageLog.add(messageModel);
                    }
                }
                listener.onMessageLogReceived(refreshedMessageLog);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    private MessageModel accessMessageModel(DataSnapshot dataSnapshot, DataSnapshot child) {
        String key = child.getKey();
        return dataSnapshot
                .child(key)
                .child("message_model")
                .getValue(MessageModel.class);
    }
}
