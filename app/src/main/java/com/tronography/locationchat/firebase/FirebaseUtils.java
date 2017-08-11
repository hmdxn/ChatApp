package com.tronography.locationchat.firebase;

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
import java.util.Objects;

import static android.content.ContentValues.TAG;
import static com.tronography.locationchat.utils.ObjectUtils.isNull;


public class FirebaseUtils {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference root = database.getReference();
    private DatabaseReference messageReference = database.getReference("messages");
    private DatabaseReference userReference = database.getReference("members");
    private HashMap<String, UserModel> userMap = new HashMap<>();
    private RetrieveUserListener retrieveUserListener;
    private RetrieveMessageLogListener retrieveMessageLogListener;

    public FirebaseUtils() {
    }

    public HashMap<String, UserModel> getUserMap() {
        return userMap;
    }

    public DatabaseReference getDatabaseReference(@NonNull String path) {
        return database.getReference(path);
    }

    public void addMessageToFirebaseDb(MessageModel messageModel) {
        //creates a unique key identifier
        HashMap<String, Object> uniqueMessageIdentifier = new HashMap<>();

        //appends root with unique key
        messageReference.updateChildren(uniqueMessageIdentifier);
        String messageId = messageReference.push().getKey();
        messageModel.setMessageId(messageId);

        //references the object using the message ID in the database
        DatabaseReference messageRoot = messageReference.child(messageId);

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

    private void applySenderNameChangesToMessage(MessageModel messageModel, UserModel userModel){
        if(Objects.equals(messageModel.getSenderId(), userModel.getId())){
            applySenderNameChangeInFirebase(messageModel, userModel.getUsername());
        }
    }
    public void applyNewUsernameInFireBase(UserModel userObject, String newUserName) {
        //reference the unique key object in the database
        DatabaseReference memberRoot = userReference.child(userObject.getId());
        userObject.setUsername(newUserName);
        //now we must generate the children of this new object
        HashMap<String, Object> userModelMap = setDatabaseUserValues(userObject);
        //confirm changes
        memberRoot.updateChildren(userModelMap);
    }

    public void applyBioDetailsInFireBase(UserModel userObject, String bio) {
        //reference the unique key object in the database
        DatabaseReference memberRoot = userReference.child(userObject.getId());
        userObject.setBio(bio);
        //now we must generate the children of this new object
        HashMap<String, Object> userModelMap = setDatabaseUserValues(userObject);
        //confirm changes
        memberRoot.updateChildren(userModelMap);
    }

    public void applySenderNameChangeInFirebase(MessageModel messagModel, String newUserName) {
        //reference the unique key object in the database
        DatabaseReference memberRoot = messageReference.child(messagModel.getMessageId());
        messagModel.setUsername(newUserName);
        //now we must generate the children of this new object
        HashMap<String, Object> userModelMap = setDatabaseMessageValues(messagModel);
        //confirm changes
        memberRoot.updateChildren(userModelMap);
    }

    public void addMessageChildEventListener(final ChatRoomActivity activity) {
        messageReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                activity.onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                activity.fireBaseOnChildChanged();
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

    public void addUserEventListener(final ChatRoomActivity activity) {
        userReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
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

    public void updateMessageSenderUsernames(final UserModel userModel) {
        messageReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    String key = child.getKey();
                    Log.e(TAG, "Message key: " + key);
                    MessageModel messageModel = dataSnapshot
                            .child(key)
                            .child("message_model")
                            .getValue(MessageModel.class);
                    Log.e(TAG, "Message Model : " + messageModel );
                    applySenderNameChangesToMessage(messageModel, userModel);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void retrieveMessagesFromFirebase(final RetrieveMessageLogListener listener) {
        this.retrieveMessageLogListener = listener;
        final ArrayList<MessageModel> refreshedMessageLog = new ArrayList<>();
        messageReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    String key = child.getKey();
                    MessageModel messageModel = dataSnapshot
                            .child(key)
                            .child("message_model")
                            .getValue(MessageModel.class);
                    Log.e(TAG, "retrieveMessagesFromFirebase: " + messageModel.getMessage());
                    refreshedMessageLog.add(messageModel);
                }
                listener.onMessageLogReceived(refreshedMessageLog);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void queryUserByID(final String userId, final RetrieveUserListener retrieveUserListener) {
        this.retrieveUserListener = retrieveUserListener;
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot
                        .child("members")
                        .child(userId)
                        .child("user_model")
                        .getValue(UserModel.class);
                if (!isNull(userModel)) {
                    retrieveUserListener.onUserRetrieved(userModel);
                } else {
                    Log.e(TAG, "onDataChange: " + "that key does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.toString());
            }
        });
    }

    public interface RetrieveUserListener {
        void onUserRetrieved(UserModel userModel);
    }

    public interface RetrieveMessageLogListener {
        void onMessageLogReceived(ArrayList<MessageModel> messageLog);
    }
}
