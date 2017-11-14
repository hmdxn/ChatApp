package com.tronography.locationchat.chatroom;

import com.tronography.locationchat.model.Message;

import java.util.ArrayList;


interface Chatroom {

    interface View {

        void setupAdapter(ArrayList<Message> messageLog);

        void setupList();

        void refreshMessageRecyclerView(ArrayList<Message> messageLog);

        void updateMessageLog(ArrayList<Message> messageLog);

        void launchUserProfileActivity(String senderID);

        void showMessage(String message);

        void setupToolBar();

        void attachFirebaseEventListeners();

        void launchLoginActivity();

        void showProgressBar();

        void hideProgressBar();

        void notifyRecyclerViewOfChanges(ArrayList<Message> messageLog);

        void scrollToBottom(ArrayList<Message> messageLog);
    }
}
