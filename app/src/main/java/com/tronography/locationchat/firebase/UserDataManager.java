package com.tronography.locationchat.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tronography.locationchat.listeners.RetrieveUserListener;
import com.tronography.locationchat.model.User;

import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.tronography.locationchat.utils.Constants.MEMBERS;
import static com.tronography.locationchat.utils.Constants.USER_MODEL;
import static com.tronography.locationchat.utils.ObjectUtils.isNull;


public class UserDataManager {

    RetrieveUserListener mRetrieveUserListener;

    public UserDataManager(RetrieveUserListener retrieveUserListener) {
        mRetrieveUserListener = retrieveUserListener;
    }

    public void saveUser(User user) {
        //creates a unique key identifier
        HashMap<String, Object> uniqueMemberIdentifier = new HashMap<>();
        //appends root with unique key
        FirebaseDatabaseReference.getUserReference().updateChildren(uniqueMemberIdentifier);

        //reference the unique key object in the database
        DatabaseReference memberRoot = FirebaseDatabaseReference.getUserReference().child(user.getId());

        //now we must generate the children of this new object
        HashMap<String, Object> userModelMap = setDatabaseUserValues(user);

        //confirm changes
        memberRoot.updateChildren(userModelMap);
    }

    public void updateBio(User user, String bio) {
        //reference the unique key object in the database
        DatabaseReference memberRoot = FirebaseDatabaseReference.getUserReference().child(user.getId());
        user.setBio(bio);
        //now we must generate the children of this new object
        HashMap<String, Object> userModelMap = setDatabaseUserValues(user);
        //confirm changes
        memberRoot.updateChildren(userModelMap);
    }

    public void queryUserByID(final String userId, final RetrieveUserListener retrieveUserListener) {
        FirebaseDatabaseReference.getRoot().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot
                        .child(MEMBERS)
                        .child(userId)
                        .child(USER_MODEL)
                        .getValue(User.class);
                if (!isNull(user)) {
                    retrieveUserListener.onUserRetrieved(user);
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

    public void updateUsername(User userObject, String newUsername) {
        //reference the unique key object in the database
        DatabaseReference memberRoot = FirebaseDatabaseReference.getUserReference().child(userObject.getId());
        userObject.setUsername(newUsername);
        //now we must generate the children of this new object
        HashMap<String, Object> userModelMap = setDatabaseUserValues(userObject);
        //confirm changes
        memberRoot.updateChildren(userModelMap);
    }

    @NonNull
    private HashMap<String, Object> setDatabaseUserValues(User user) {
        HashMap<String, Object> userModelMap = new HashMap<>();
        userModelMap.put(USER_MODEL, user);
        return userModelMap;
    }
}
