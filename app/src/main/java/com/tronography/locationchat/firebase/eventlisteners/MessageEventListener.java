package com.tronography.locationchat.firebase.eventlisteners;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.tronography.locationchat.chatroom.ChatPresenter;
import com.tronography.locationchat.firebase.FirebaseDatabaseReference;
import com.tronography.locationchat.model.Message;

/**
 * Created by jonathancolon on 8/27/17.
 */

public class MessageEventListener {

    private static final String TAG = MessageEventListener.class.getSimpleName();

    public void addMessageChildEventListener(final ChatPresenter presenter, String roomID) {
        FirebaseDatabaseReference.getChatRoomMessageRef(roomID)
                .addChildEventListener(new ChildEventListener() {

            @Override  //TODO fix being called three times
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children) {
                    Log.i(TAG, "onMessageAdded: " + child.getValue(Message.class));
                    Message message = child.getValue(Message.class);
                    presenter.onMessageAdded(message);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                presenter.onMessageLogChanged(dataSnapshot, s);
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
