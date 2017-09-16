package com.tronography.locationchat.chatroom;

import com.google.firebase.database.DataSnapshot;
import com.tronography.locationchat.model.MessageModel;


public class ChatContract {

    public interface View {

        void sendMessage();

        void messagesOnChildAdded(DataSnapshot dataSnapshot, String s);

        void messagesOnChildChanged();

        void launchUserProfileActivity(String senderID);

        void showSenderId(MessageModel message);

        void signOut();
    }

    public interface UserActionListener {

        void send();

        void messagesOnChildAdded(DataSnapshot dataSnapshot, String s);

        void childChanged(DataSnapshot dataSnapshot, String s);

        void messageClicked(MessageModel message);

        void messageLongClicked(MessageModel message);

        void onSignOutClicked();
    }
}
