package com.tronography.locationchat.database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tronography.locationchat.listeners.RetrieveUserListener;
import com.tronography.locationchat.model.UserModel;

import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.tronography.locationchat.database.firebase.FirebaseDatabaseReference.getRoot;
import static com.tronography.locationchat.database.firebase.FirebaseDatabaseReference.getUserReference;
import static com.tronography.locationchat.utils.ObjectUtils.isNull;


public class UserDao implements UserDaoContract {

    @Override
    public void saveUser(UserModel userModel) {
        //creates a unique key identifier
        HashMap<String, Object> uniqueMemberIdentifier = new HashMap<>();
        //appends root with unique key
        getUserReference().updateChildren(uniqueMemberIdentifier);

        //reference the unique key object in the database
        DatabaseReference memberRoot = getUserReference().child(userModel.getId());

        //now we must generate the children of this new object
        HashMap<String, Object> userModelMap = setDatabaseUserValues(userModel);

        //confirm changes
        memberRoot.updateChildren(userModelMap);
    }

    @Override
    public void updateBio(UserModel userModel, String bio) {
        //reference the unique key object in the database
        DatabaseReference memberRoot = getUserReference().child(userModel.getId());
        userModel.setBio(bio);
        //now we must generate the children of this new object
        HashMap<String, Object> userModelMap = setDatabaseUserValues(userModel);
        //confirm changes
        memberRoot.updateChildren(userModelMap);
    }

    @Override
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

    @Override
    public void updateUsername(UserModel userObject, String newUsername) {
        //reference the unique key object in the database
        DatabaseReference memberRoot = getUserReference().child(userObject.getId());
        userObject.setUsername(newUsername);
        //now we must generate the children of this new object
        HashMap<String, Object> userModelMap = setDatabaseUserValues(userObject);
        //confirm changes
        memberRoot.updateChildren(userModelMap);
    }

    @NonNull
    private HashMap<String, Object> setDatabaseUserValues(UserModel userModel) {
        HashMap<String, Object> userModelMap = new HashMap<>();
        userModelMap.put("user_model", userModel);
        return userModelMap;
    }
}
