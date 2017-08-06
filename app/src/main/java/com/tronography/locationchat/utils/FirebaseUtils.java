package com.tronography.locationchat.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tronography.locationchat.model.MessageModel;
import com.tronography.locationchat.model.UserModel;
import com.tronography.locationchat.ui.ChatRoomActivity;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;


public class FirebaseUtils {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference messageReference = database.getReference("messages");
    private DatabaseReference userReference = database.getReference("members");
    private ArrayList<MessageModel> chatLog = new ArrayList<>();
    private HashMap<String, UserModel> userMap = new HashMap<>();
    private Listener listener;

    public FirebaseUtils() {
    }

    public FirebaseUtils(Listener listener) {
        this.listener = listener;
    }

    public ArrayList<MessageModel> getChatLog() {
        return chatLog;
    }

    public HashMap<String, UserModel> getUserMap() {
        return userMap;
    }

    public DatabaseReference getDatabaseReference(@NonNull String path) {
        return database.getReference(path);
    }

    public void addMessageToFirebaseDb(MessageModel messageModel, UserModel user) {
        //creates a unique key identifier
        HashMap<String, Object> uniqueMessageIdentifier = new HashMap<>();

        //appends root with unique key
        messageReference.updateChildren(uniqueMessageIdentifier);

        //references the unique key object in the database
        DatabaseReference messageRoot = messageReference.child(messageModel.getMessageId());

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

    public void addUserToFirebase(UserModel userObject) {
        //creates a unique key identifier
        HashMap<String, Object> uniqueMemberIdentifier = new HashMap<>();

        //appends root with unique key
        userReference.updateChildren(uniqueMemberIdentifier);

        //reference the unique key object in the database
        DatabaseReference memberRoot = userReference.child(userObject.getId());

        //now we must generate the children of this new object
        HashMap<String, Object> userModelMap = setDatabaseUserValues(userObject);

        //confirm changes
        memberRoot.updateChildren(userModelMap);
    }

    @NonNull
    private HashMap<String, Object> setDatabaseUserValues(UserModel userObject) {
        HashMap<String, Object> userModelMap = new HashMap<>();
        userModelMap.put("user_model", userObject);
        return userModelMap;
    }

    public void addMessageRefChildEventListener(final ChatRoomActivity activity) {
        messageReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    MessageModel messageModel = child.getValue(MessageModel.class);
                    chatLog.add(messageModel);
                    activity.refreshMessageLog();
                }
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

    public void addUserRefChildEventListener() {
        userReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    UserModel userModel = child.getValue(UserModel.class);
                    userMap.put(userModel.getId(), userModel);
                    Log.e(TAG, "onChildAdded: " + userModel.getId());
                }
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

    public void querySpecificUser(final String userId) {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.getKey();

        final UserModel[] user = new UserModel[1];

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot
                        .child("members")
                        .child(userId)
                        .child("user_model")
                        .getValue(UserModel.class);

                user[0] = userModel;


                if (!ObjectUtils.isNull(user[0])) {
                    listener.onUserRetrieved(user[0]);
                }
                Log.e(TAG, "onDataChange: " + user[0].getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.toString());
            }
        });
    }

    public interface Listener {
        void onUserRetrieved(UserModel userModel);
    }
}
