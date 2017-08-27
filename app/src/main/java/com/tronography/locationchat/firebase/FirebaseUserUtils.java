package com.tronography.locationchat.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tronography.locationchat.chatroom.ChatContract;
import com.tronography.locationchat.model.UserModel;

import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.tronography.locationchat.firebase.FirebaseDatabaseReference.getRoot;
import static com.tronography.locationchat.firebase.FirebaseDatabaseReference.getUserReference;
import static com.tronography.locationchat.utils.ObjectUtils.isNull;


public class FirebaseUserUtils {


    public void queryUserByID(final String userId, final RetrieveUserListener retrieveUserListener) {
        getRoot().addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void addUserEventListener(final ChatContract.UserActionListener presenter) {
        getUserReference().addChildEventListener(new ChildEventListener() {
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

    public void applyBioDetailsInFireBase(UserModel userObject, String bio) {
        //reference the unique key object in the database
        DatabaseReference memberRoot = getUserReference().child(userObject.getId());
        userObject.setBio(bio);
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

    public void applyNewUsernameInFireBase(UserModel userObject, String newUserName) {
        //reference the unique key object in the database
        DatabaseReference memberRoot = getUserReference().child(userObject.getId());
        userObject.setUsername(newUserName);
        //now we must generate the children of this new object
        HashMap<String, Object> userModelMap = setDatabaseUserValues(userObject);
        //confirm changes
        memberRoot.updateChildren(userModelMap);
    }

    public void addUserToFirebase(UserModel userObject) {
        //creates a unique key identifier
        HashMap<String, Object> uniqueMemberIdentifier = new HashMap<>();

        //appends root with unique key
        getUserReference().updateChildren(uniqueMemberIdentifier);

        //reference the unique key object in the database
        DatabaseReference memberRoot = getUserReference().child(userObject.getId());

        //now we must generate the children of this new object
        HashMap<String, Object> userModelMap = setDatabaseUserValues(userObject);

        //confirm changes
        memberRoot.updateChildren(userModelMap);
    }

    public interface RetrieveUserListener {
        void onUserRetrieved(UserModel userModel);
    }
}
