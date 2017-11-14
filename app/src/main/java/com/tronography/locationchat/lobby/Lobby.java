package com.tronography.locationchat.lobby;

import com.tronography.locationchat.model.Chatroom;


interface Lobby {

    interface View {

        void launchLoginActivity();

        void launchChatroomActivity(String roomName);

        void showChatroomDetails(String chatroomName);

        void showCurrentLocationDetails(String location);
    }
}
