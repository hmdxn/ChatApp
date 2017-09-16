package com.tronography.locationchat.lobby;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.tronography.locationchat.model.ChatRoomModel;

import java.util.ArrayList;


public class LobbyContract {

    public interface View {

        void updateUI(FirebaseUser user);

        void loadReturningUser(String userID);

        void launchLoginActivity();

        void setupAdapter();

        void setupList();

        void addRoom();

        void chatRoomOnChildAdded(DataSnapshot dataSnapshot, String s);

        void chatRoomOnChildChanged();

        void launchChatRoomActivity(String roomID, String roomName);

        void refreshChatRoomRecyclerView(ArrayList<ChatRoomModel> chatRoomList);
    }

    public interface UserActionListener {

        void addRoomClicked();

        void childAdded(DataSnapshot dataSnapshot, String s);

        void childChanged(DataSnapshot dataSnapshot, String s);

        void chatRoomClicked(ChatRoomModel chatRoom);

    }
}
