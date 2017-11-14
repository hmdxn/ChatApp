package com.tronography.locationchat.firebase.datamangers;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tronography.locationchat.firebase.utils.FirebaseDatabaseReference;
import com.tronography.locationchat.firebase.utils.UpdateMessageSenderName;
import com.tronography.locationchat.listeners.RetrieveMessageLogListener;
import com.tronography.locationchat.model.Message;
import com.tronography.locationchat.model.User;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.tronography.locationchat.utils.Constants.Firebase.MESSAGE_MODEL;
import static com.tronography.locationchat.utils.ObjectUtils.isNull;


public class MessageDataManager {

    private RetrieveMessageLogListener mListener;


    public MessageDataManager(RetrieveMessageLogListener listener) {
        mListener = listener;
    }

    public void getMessageLog(String roomID) {
        final ArrayList<Message> refreshedMessageLog = new ArrayList<>();
        FirebaseDatabaseReference.getChatRoomMessageRef(roomID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {

                    String key = child.getKey();
                    Message message = dataSnapshot
                            .child(key)
                            .child(MESSAGE_MODEL)
                            .getValue(Message.class);

                    if (!isNull(message)) {
                        Log.e(TAG, "getMessageLog: " + message.getMessage());
                        refreshedMessageLog.add(message);
                    }
                }
                mListener.onMessageLogReceived(refreshedMessageLog);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    public void updateSenderName(User user) {
        UpdateMessageSenderName updateMessageSenderName = new UpdateMessageSenderName(user);
        updateMessageSenderName.applyChanges();
    }

    public void saveMessage(Message message, String roomID) {
        //creates a unique key identifier
        HashMap<String, Object> uniqueMessageIdentifier = new HashMap<>();

        //appends root with unique key
        FirebaseDatabaseReference.getChatRoomMessageRef(roomID).updateChildren(uniqueMessageIdentifier);

        //set push key to message ID
        String messageId = FirebaseDatabaseReference.getMessageReference().push().getKey();
        message.setMessageId(messageId);

        //references the object using the message ID in the database
        DatabaseReference messageRoot = FirebaseDatabaseReference.getChatRoomMessageRef(roomID).child(messageId);

        //assigns values to the children of this new message object
        HashMap<String, Object> messageModelMap = new HashMap<>();
        messageModelMap.put(MESSAGE_MODEL, message);

        //confirm changes
        messageRoot.updateChildren(messageModelMap);
    }

}
