package com.tronography.locationchat.database.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.tronography.locationchat.chatroom.ChatContract;

/**
 * Created by jonathancolon on 8/27/17.
 */

public class MessageEventListener {

    public void addMessageChildEventListener(final ChatContract.UserActionListener presenter, String roomID) {
        FirebaseDatabaseReference.getChatRoomMessageRef(roomID)
                .addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                presenter.messagesOnChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                presenter.childChanged(dataSnapshot, s);
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
