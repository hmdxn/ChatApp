package com.tronography.locationchat.chatroom;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.tronography.locationchat.model.MessageModel;

import static com.tronography.locationchat.utils.ObjectUtils.isNull;


public class ChatPresenter implements ChatContract.UserActionListener {

    private ChatContract.View view;

    ChatPresenter(@NonNull ChatContract.View view) {
        this.view = view;
    }

    @Override
    public void send() {
        view.sendMessage();
    }

    @Override
    public void messagesOnChildAdded(DataSnapshot dataSnapshot, String s) {
        view.messagesOnChildAdded(dataSnapshot, s);
    }

    @Override
    public void childChanged(DataSnapshot dataSnapshot, String s) {
        view.messagesOnChildChanged();
    }

    @Override
    public void messageClicked(MessageModel message) {
        if ((!isNull(message.getSenderId()))) {
            view.launchUserProfileActivity(message.getSenderId());
        }
    }

    @Override
    public void messageLongClicked(MessageModel message) {
        view.showSenderId(message);
    }

    @Override
    public void onSignOutClicked(){
        view.signOut();
    }
}
