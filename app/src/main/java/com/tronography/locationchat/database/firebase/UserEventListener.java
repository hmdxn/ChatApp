package com.tronography.locationchat.database.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.tronography.locationchat.chatroom.ChatContract;

import static com.tronography.locationchat.database.firebase.FirebaseDatabaseReference.getUserReference;

/**
 * Created by jonathancolon on 8/27/17.
 */

public class UserEventListener {

    public void addUserChildEventListener(final ChatContract.UserActionListener presenter) {

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
}
