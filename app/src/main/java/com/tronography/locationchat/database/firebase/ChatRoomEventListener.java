package com.tronography.locationchat.database.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.tronography.locationchat.lobby.LobbyContract;

import static com.tronography.locationchat.database.firebase.FirebaseDatabaseReference.getChatRoomReference;

/**
 * Created by jonathancolon on 8/30/17.
 */

public class ChatRoomEventListener {
    public void addChatRoomEventListener(final LobbyContract.UserActionListener presenter) {
        getChatRoomReference().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                presenter.childAdded(dataSnapshot, s);
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
