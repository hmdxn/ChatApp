package com.tronography.locationchat.firebase.eventlisteners;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.tronography.locationchat.firebase.utils.FirebaseDatabaseReference;
import com.tronography.locationchat.lobby.LobbyPresenter;

/**
 * Created by jonathancolon on 8/30/17.
 */

public class ChatroomEventListener {

    public void addChatroomEventListener(final LobbyPresenter presenter) {
        FirebaseDatabaseReference.getChatRoomReference().addChildEventListener(new ChildEventListener() {
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
