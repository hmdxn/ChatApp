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
import com.tronography.locationchat.BaseActivity;
import com.tronography.locationchat.R;
import com.tronography.locationchat.chatroom.ChatRoomActivity;
import com.tronography.locationchat.database.UserDao;
import com.tronography.locationchat.database.firebase.ChatRoomDoa;
import com.tronography.locationchat.database.firebase.ChatRoomEventListener;
import com.tronography.locationchat.listeners.RetrieveChatRoomListener;
import com.tronography.locationchat.lobby.ChatRoomAdapter.Listener;
import com.tronography.locationchat.login.LoginActivity;
import com.tronography.locationchat.model.ChatRoomModel;
import com.tronography.locationchat.utils.SharedPrefsUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tronography.locationchat.utils.ObjectUtils.isEmpty;


public class LobbyActivity extends BaseActivity implements LobbyContract.View, Listener,
        RetrieveChatRoomListener {

    @BindView(R.id.btn_add_room)
    Button add_room;

    @BindView(R.id.room_name_edittext)
    EditText room_name;
    ChatRoomAdapter adapter;
    ChatRoomDoa chatRoomDoa;
    @BindView(R.id.chatroom_rv)
    RecyclerView chatRoomRV;

    private LobbyContract.UserActionListener presenter;
    private ArrayList<ChatRoomModel> chatRoomList;
    private FirebaseAuth mAuth;
    private UserDao userDao = new UserDao();

    public static Intent provideIntent(Context context) {
        return new Intent(context, LobbyActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        chatRoomDoa = new ChatRoomDoa();

        initializePresenter();
        setupAdapter();
        setupList();

        ChatRoomEventListener chatRoomEventListener = new ChatRoomEventListener();
        chatRoomEventListener.addChatRoomEventListener(presenter);
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
        SharedPrefsUtils.CURRENT_USER_KEY = userID;
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
        chatRoomList = new ArrayList<>();
        adapter = new ChatRoomAdapter(this);
        adapter.setData(chatRoomList);
    }

    @Override
    public void setupList() {
        chatRoomRV.setLayoutManager(new LinearLayoutManager(chatRoomRV.getContext()));
        chatRoomRV.setAdapter(adapter);
    }

    @Override
    public void addRoom() {
        String roomName = room_name.getText().toString();
        if (!isEmpty(roomName)) {
            ChatRoomModel newChatRoom = new ChatRoomModel(roomName);
            chatRoomDoa.addChatRoomToFirebase(newChatRoom);
            chatRoomDoa.addNewConversationToMessages(newChatRoom);
        }
    }

    @Override
    public void chatRoomOnChildAdded(DataSnapshot dataSnapshot, String s) {
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        for (DataSnapshot child : children) {
            ChatRoomModel chatRoomModel = child.getValue(ChatRoomModel.class);
            chatRoomList.add(chatRoomModel);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void chatRoomOnChildChanged() {
        chatRoomDoa.retrieveChatRoomsFromFirebase(this);
    }

    @OnClick(R.id.btn_add_room)
    public void onClick() {
        presenter.addRoomClicked();
    }

    @Override
    public void onMessageLongClicked(ChatRoomModel chatRoom) {
        Toast.makeText(this, chatRoom.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChatRoomClicked(ChatRoomModel chatRoom) {
        launchChatRoomActivity(chatRoom.getId(), chatRoom.getName());
    }

    @Override
    public void launchChatRoomActivity(String roomID, String roomName) {
        Intent intent = ChatRoomActivity.provideIntent(this, roomID, roomName);
        startActivity(intent);
    }

    @Override
    public void onChatRoomListRetrieved(ArrayList<ChatRoomModel> refreshedChatRoomList) {
        chatRoomList = refreshedChatRoomList;
        refreshChatRoomRecyclerView(chatRoomList);
    }

    @Override
    public void refreshChatRoomRecyclerView(ArrayList<ChatRoomModel> chatRoomList) {
        ChatRoomAdapter adapter = (ChatRoomAdapter) chatRoomRV.getAdapter();
        adapter.setData(chatRoomList);
    }

    @Override
    public void onChatRoomRetrieved(ChatRoomModel chatRoomModel) {
        //// TODO: 8/23/17 no reason for querying a single chatroom at this time.
    }
}
