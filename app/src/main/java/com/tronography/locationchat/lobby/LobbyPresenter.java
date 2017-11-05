package com.tronography.locationchat.lobby;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.tronography.locationchat.model.ChatRoom;

import static com.tronography.locationchat.utils.ObjectUtils.isNull;


public class LobbyPresenter implements LobbyContract.UserActionListener {

    private LobbyContract.View view;

    public LobbyPresenter(@NonNull LobbyContract.View view) {
        this.view = view;
    }

    @Override
    public void addRoomClicked() {
        view.addRoom();
    }

    @Override
    public void childAdded(DataSnapshot dataSnapshot, String s) {
        view.chatRoomOnChildAdded(dataSnapshot, s);
    }

    @Override
    public void childChanged(DataSnapshot dataSnapshot, String s) {
        view.chatRoomOnChildChanged();
    }

    @Override
    public void chatRoomClicked(ChatRoom chatRoom) {
        if ((!isNull(chatRoom))) {
            view.launchChatRoomActivity(chatRoom.getId(), chatRoom.getName());
        }
    }
}
