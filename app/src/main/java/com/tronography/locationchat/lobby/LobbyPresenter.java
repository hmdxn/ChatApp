package com.tronography.locationchat.lobby;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.tronography.locationchat.firebase.datamangers.ChatRoomDataManager;
import com.tronography.locationchat.listeners.RetrieveChatRoomListener;
import com.tronography.locationchat.model.ChatRoom;

import javax.inject.Inject;

import static com.tronography.locationchat.utils.ObjectUtils.isEmpty;
import static com.tronography.locationchat.utils.ObjectUtils.isNull;


public class LobbyPresenter implements RetrieveChatRoomListener {

    private Lobby.View view;
    private ChatRoomDataManager chatRoomDataManager = new ChatRoomDataManager(this);

    @Inject
    public LobbyPresenter() {
    }

    public void childAdded(DataSnapshot dataSnapshot, String s) {
    }

    public void enterChatroom(String chatroomName) {
        if ((!isEmpty(chatroomName))) {
            view.launchChatroomActivity(chatroomName);
        }
    }

    @Override
    public void onChatRoomRetrieved(ChatRoom chatRoom) {
        Log.e("TAG", "onChatRoomRetrieved: " + chatRoom.toString());
        view.showChatroomDetails(chatRoom.getName());
    }

    void verifyUserAuth() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (isNull(user)) {
            view.launchLoginActivity();
        }
    }

    public void setView(Lobby.View view) {
        this.view = view;
    }

    public void retrieveChatroom(String postalCode) {
        chatRoomDataManager.retrieveChatroomByPostalCode(postalCode);
    }
}
