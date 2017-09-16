package com.tronography.locationchat.chatroom;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.tronography.locationchat.BaseActivity;
import com.tronography.locationchat.R;
import com.tronography.locationchat.database.MessageDoa;
import com.tronography.locationchat.database.UserDao;
import com.tronography.locationchat.database.firebase.MessageEventListener;
import com.tronography.locationchat.database.firebase.UserEventListener;
import com.tronography.locationchat.listeners.RetrieveMessageLogListener;
import com.tronography.locationchat.listeners.RetrieveUserListener;
import com.tronography.locationchat.login.LoginActivity;
import com.tronography.locationchat.model.MessageModel;
import com.tronography.locationchat.model.UserModel;
import com.tronography.locationchat.userprofile.UserProfileActivity;
import com.tronography.locationchat.utils.SharedPrefsUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tronography.locationchat.utils.EditTextUtils.clearText;
import static com.tronography.locationchat.utils.ObjectUtils.isEmpty;
import static com.tronography.locationchat.utils.ObjectUtils.isNull;

public class ChatRoomActivity extends BaseActivity implements ChatContract.View,
        MessageAdapter.Listener, RetrieveUserListener, RetrieveMessageLogListener {

    private static final String ROOM_ID_KEY = "room_id";
    private static final String ROOM_NAME_KEY = "room_name";
    private final String TAG = ChatRoomActivity.class.getSimpleName();

    @BindView(R.id.chat_input_et)
    EditText editText;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private UserModel user;
    private MessageDoa messageDao = new MessageDoa();
    private String userID;
    private MessageAdapter adapter;
    private ArrayList<MessageModel> messageLog;
    private ChatContract.UserActionListener presenter;
    private UserDao userDao = new UserDao();
    private String roomID;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String roomName;

    public static Intent provideIntent(Context context) {
        return new Intent(context, ChatRoomActivity.class);
    }

    public static Intent provideIntent(Context context, String roomID, String roomName) {
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(ROOM_ID_KEY, roomID);
        intent.putExtra(ROOM_NAME_KEY, roomName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatroom_activity);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        roomID = getIntent().getStringExtra(ROOM_ID_KEY);
        roomName = getIntent().getStringExtra(ROOM_NAME_KEY);

        initializePresenter();
        setupAdapter();
        setupList();

        MessageEventListener messageEventListener = new MessageEventListener();
        messageEventListener.addMessageChildEventListener(presenter, roomID);
        Log.e(TAG, "onCreate: " + "MessageEventListenerAdded");

        UserEventListener userEventListener = new UserEventListener();
        userEventListener.addUserChildEventListener(presenter);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        //todo Make more efficent. Perhaps extract this to another class?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            myToolbar.setTitleTextColor(getColor(R.color.light_font));
        }

        setSupportActionBar(myToolbar);

        setTitle(" Room : " + roomName);

        if (!isNull(userID)) {
            userDao.queryUserByID(userID, this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        checkIfUserIsLoggedIn();
    }

    private void checkIfUserIsLoggedIn() {
        firebaseUser = mAuth.getCurrentUser();
        updateUI(firebaseUser);
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            userID = user.getUid();
            loadReturningUser(userID);
        } else {
            launchLoginActivity();
        }
    }

    private void loadReturningUser(String userID) {
        SharedPrefsUtils.CURRENT_USER_KEY = userID;
        userDao.queryUserByID(userID, this);
    }

    public void launchLoginActivity() {
        Intent intent = LoginActivity.provideIntent(this);
        startActivity(intent);
        finish();
    }

    private void initializePresenter() {
        if (presenter == null) presenter = new ChatPresenter(this);
    }

    private void setupAdapter() {
        messageLog = new ArrayList<>();
        adapter = new MessageAdapter(this);
        adapter.setData(messageLog);
    }

    private void setupList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " + userID);
        loadReturningUser(userID);
        messageDao.getMessageLog(this, roomID);
    }

    @Override
    public void onMessageLongClicked(MessageModel message) {
        presenter.messageLongClicked(message);
    }

    @Override
    public void onMessageClicked(MessageModel message) {
        presenter.messageClicked(message);
    }

    @OnClick(R.id.send_button_iv)
    public void submit() {
        presenter.send();
    }

    @Override
    public void onUserRetrieved(UserModel userModel) {
        user = userModel;
    }

    @Override
    public void onMessageLogReceived(ArrayList<MessageModel> refreshedMessageLog) {
        Log.e(TAG, "onMessageLogReceived: " + "RECEIVED");
        messageLog = refreshedMessageLog;
        System.out.println("refreshedMessageLog = " + refreshedMessageLog);
        refreshMessageRecyclerView(messageLog);
    }

    public void refreshMessageRecyclerView(ArrayList<MessageModel> messageLog) {
        MessageAdapter adapter = (MessageAdapter) recyclerView.getAdapter();
        adapter.setData(messageLog);
    }

    @Override
    public void sendMessage() {
        String message = editText.getText().toString();
        if (!isEmpty(message)) {
            messageDao.saveMessage(new MessageModel(message, user.getId(),
                    user.getUsername()), roomID);
            clearText(editText, this);
        }
    }

    @Override
    public void messagesOnChildAdded(DataSnapshot dataSnapshot, String s) {
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        Log.e(TAG, "messagesOnChildAdded: " + dataSnapshot.getKey());
        for (DataSnapshot child : children) {
            Log.e(TAG, "messagesOnChildAdded: " + child.getKey());
            Log.e(TAG, "messagesOnChildAdded: " + child.child("message_model").getValue(MessageModel.class));
            MessageModel messageModel = child.getValue(MessageModel.class);
            messageLog.add(messageModel);
            recyclerView.scrollToPosition(messageLog.size() - 1);
            adapter.notifyItemInserted(messageLog.size() - 1);
        }
    }

    @Override
    public void messagesOnChildChanged() {
        messageDao.getMessageLog(this, roomID);
    }

    @Override
    public void launchUserProfileActivity(String senderID) {
        Intent intent = UserProfileActivity.provideIntent(this, senderID);
        startActivity(intent);
    }

    @Override
    public void showSenderId(MessageModel message) {
        Toast.makeText(this, message.getSenderId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                presenter.onSignOutClicked();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
