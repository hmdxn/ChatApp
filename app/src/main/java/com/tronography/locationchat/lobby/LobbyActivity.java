package com.tronography.locationchat.lobby;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.tronography.locationchat.ChatApplication;
import com.tronography.locationchat.common.BaseActivity;
import com.tronography.locationchat.R;
import com.tronography.locationchat.chatroom.ChatActivity;
import com.tronography.locationchat.firebase.datamanagers.ChatRoomDataManager;
import com.tronography.locationchat.firebase.eventlisteners.ChatroomEventListener;
import com.tronography.locationchat.listeners.RetrieveChatRoomListener;
import com.tronography.locationchat.lobby.LobbyAdapter.Listener;
import com.tronography.locationchat.login.LoginActivity;
import com.tronography.locationchat.model.ChatRoom;
import com.tronography.locationchat.utils.SharedPrefsUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tronography.locationchat.utils.ObjectUtils.isEmpty;
import static com.tronography.locationchat.utils.SharedPrefsUtils.CURRENT_USER_KEY;


public class LobbyActivity extends BaseActivity implements LobbyContract.View, Listener,
        RetrieveChatRoomListener {

    @Bind(R.id.chatroom_rv)
    RecyclerView chatroomRV;

    @Bind(R.id.btn_add_room)
    Button add_room;

    @Bind(R.id.room_name_edittext)
    EditText room_name;

    LobbyAdapter adapter;
    ChatRoomDataManager chatRoomDataManager;

    private LobbyContract.UserActionListener presenter;
    private ArrayList<ChatRoom> chatrooms;
    private FirebaseAuth mAuth;


    public static Intent provideIntent(Context context) {
        return new Intent(context, LobbyActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        chatRoomDataManager = new ChatRoomDataManager(this);

        initializePresenter();
        setupAdapter();
        setupList();

        ChatroomEventListener chatroomEventListener = new ChatroomEventListener();
        chatroomEventListener.addChatroomEventListener(presenter);

    }

    @Override
    public void onStart() {
        super.onStart();
        checkIfUserIsLoggedIn();
    }

    private void checkIfUserIsLoggedIn() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        updateUI(firebaseUser);
    }

    @Override
    public void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            String userID = user.getUid();
            loadReturningUser(userID);
        } else {
            launchLoginActivity();
        }
    }

    @Override
    public void loadReturningUser(String userID) {
        CURRENT_USER_KEY = userID;
    }

    @Override
    public void launchLoginActivity() {
        Intent intent = LoginActivity.provideIntent(this);
        startActivity(intent);
        finish();
    }

    private void initializePresenter() {
        if (presenter == null) presenter = new LobbyPresenter(this);
    }

    @Override
    public void setupAdapter() {
        chatrooms = new ArrayList<>();
        adapter = new LobbyAdapter(this);
        adapter.setData(chatrooms);
    }

    @Override
    public void setupList() {
        chatroomRV.setLayoutManager(new LinearLayoutManager(chatroomRV.getContext()));
        chatroomRV.setAdapter(adapter);
    }

    @Override
    public void addRoom() {
        String roomName = room_name.getText().toString();
        if (!isEmpty(roomName)) {
            ChatRoom newChatroom = new ChatRoom(roomName);
            chatRoomDataManager.addChatroomToFirebase(newChatroom);
        }
    }

    @Override
    public void chatRoomOnChildAdded(DataSnapshot dataSnapshot, String s) {
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        for (DataSnapshot child : children) {
            ChatRoom chatRoom = child.getValue(ChatRoom.class);
            chatrooms.add(chatRoom);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void chatRoomOnChildChanged() {
        chatRoomDataManager.retrieveFromFirebase();
    }

    @OnClick(R.id.btn_add_room)
    public void onClick() {
        presenter.addRoomClicked();
    }

    @Override
    public void onMessageLongClicked(ChatRoom chatRoom) {
        Toast.makeText(this, chatRoom.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChatRoomClicked(ChatRoom chatRoom) {
        launchChatRoomActivity(chatRoom.getId(), chatRoom.getName());
    }

    @Override
    public void launchChatRoomActivity(String roomID, String roomName) {
        Intent intent = ChatActivity.provideIntent(this, roomID, roomName);
        startActivity(intent);
    }

    @Override
    public void onChatRoomListRetrieved(ArrayList<ChatRoom> refreshedChatRoomList) {
        chatrooms = refreshedChatRoomList;
        refreshChatRoomRecyclerView(chatrooms);
    }

    @Override
    public void refreshChatRoomRecyclerView(ArrayList<ChatRoom> chatRoomList) {
        LobbyAdapter adapter = (LobbyAdapter) chatroomRV.getAdapter();
        adapter.setData(chatRoomList);
    }

    @Override
    public void onChatRoomRetrieved(ChatRoom chatRoom) {
        //// TODO: 8/23/17 no reason for querying a single chatroom at this time.
    }
}
