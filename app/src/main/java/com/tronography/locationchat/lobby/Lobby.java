package com.tronography.locationchat.lobby;


interface Lobby {

    interface View {

        void launchLoginActivity();

        void launchChatroomActivity(String roomName);

        void showChatroomDetails(String chatroomName);

        void showCurrentLocationDetails(String location);
    }
}
