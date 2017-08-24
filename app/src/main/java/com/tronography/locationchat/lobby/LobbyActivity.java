package com.tronography.locationchat.lobby;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.tronography.locationchat.BaseActivity;
import com.tronography.locationchat.R;
import com.tronography.locationchat.chatroom.ChatRoomActivity;
import com.tronography.locationchat.firebase.FirebaseChatRoomUtils;
import com.tronography.locationchat.lobby.ChatRoomAdapter.Listener;
import com.tronography.locationchat.model.ChatRoomModel;

import java.util.ArrayList;

import static com.tronography.locationchat.utils.ObjectUtils.isEmpty;


public class LobbyActivity extends BaseActivity implements LobbyContract.View, Listener, FirebaseChatRoomUtils.RetrieveChatRoomListener {

    FirebaseChatRoomUtils chatRoomUtils = new FirebaseChatRoomUtils();

    private Button add_room;
    private EditText room_name;

    private LobbyContract.UserActionListener presenter;
    private ArrayList<ChatRoomModel> chatRoomList;
    private ChatRoomAdapter adapter;
    private RecyclerView chatRoomRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        initializePresenter();
        chatRoomRV = (RecyclerView) findViewById(R.id.chatroom_rv);
        setupAdapter();
        setupList();

        add_room = (Button) findViewById(R.id.btn_add_room);
        room_name = (EditText) findViewById(R.id.room_name_edittext);

        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.addRoomClicked();
            }
        });

        chatRoomUtils.addChatRoomEventListener(presenter);
    }

    private void initializePresenter() {
        if (presenter == null) presenter = new LobbyPresenter(this);
    }

    private void setupAdapter() {
        chatRoomList = new ArrayList<>();
        adapter = new ChatRoomAdapter(this);
        adapter.setData(chatRoomList);
    }

    private void setupList() {
        chatRoomRV.setLayoutManager(new LinearLayoutManager(chatRoomRV.getContext()));
        chatRoomRV.setAdapter(adapter);
    }

    @Override
    public void addRoom() {
        String roomName = room_name.getText().toString();
        if (!isEmpty(roomName)) {
            ChatRoomModel newChatRoom = new ChatRoomModel(roomName);
            chatRoomUtils.addChatRoomToFirebase(newChatRoom);
            chatRoomUtils.addNewConversationToMessages(newChatRoom);
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
        chatRoomUtils.retrieveChatRoomsFromFirebase(this);
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

    public void refreshChatRoomRecyclerView(ArrayList<ChatRoomModel> chatRoomList) {
        ChatRoomAdapter adapter = (ChatRoomAdapter) chatRoomRV.getAdapter();
        adapter.setData(chatRoomList);
    }

    @Override
    public void onChatRoomRetrieved(ChatRoomModel chatRoomModel) {
        //// TODO: 8/23/17 no reason for querying a single chatroom at this time.
    }
}
