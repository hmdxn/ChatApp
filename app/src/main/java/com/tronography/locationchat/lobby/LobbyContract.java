package com.tronography.locationchat.lobby;

import com.google.firebase.database.DataSnapshot;
import com.tronography.locationchat.model.ChatRoomModel;


public class LobbyContract {

    public interface View {

        void addRoom();

        void chatRoomOnChildAdded(DataSnapshot dataSnapshot, String s);

        void chatRoomOnChildChanged();

        void launchChatRoomActivity(String roomID, String roomName);
    }

    public interface UserActionListener {

        void addRoomClicked();

        void childAdded(DataSnapshot dataSnapshot, String s);

        void childChanged(DataSnapshot dataSnapshot, String s);

        void chatRoomClicked(ChatRoomModel chatRoom);

    }
}
