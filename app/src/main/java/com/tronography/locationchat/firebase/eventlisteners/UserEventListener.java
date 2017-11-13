package com.tronography.locationchat.firebase.eventlisteners;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.tronography.locationchat.chatroom.ChatPresenter;
import com.tronography.locationchat.firebase.utils.FirebaseDatabaseReference;

/**
 * Created by jonathancolon on 8/27/17.
 */

public class UserEventListener {

    public void addUserChildEventListener(final ChatPresenter presenter) {

        FirebaseDatabaseReference.getUserReference().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
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
