package com.tronography.locationchat.chatroom;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.tronography.locationchat.BaseActivity;
import com.tronography.locationchat.R;
import com.tronography.locationchat.firebase.FirebaseMessageUtils;
import com.tronography.locationchat.firebase.FirebaseUserUtils;
import com.tronography.locationchat.login.LoginActivity;
import com.tronography.locationchat.model.MessageModel;
import com.tronography.locationchat.model.UserModel;
import com.tronography.locationchat.userprofile.UserProfileActivity;
import com.tronography.locationchat.utils.SharedPrefsUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tronography.locationchat.firebase.FirebaseMessageUtils.RetrieveMessageLogListener;
import static com.tronography.locationchat.userprofile.UserProfileActivity.SENDER_ID_KEY;
import static com.tronography.locationchat.utils.EditTextUtils.clearText;
import static com.tronography.locationchat.utils.ObjectUtils.isNull;
import static com.tronography.locationchat.utils.SharedPrefsUtils.CURRENT_USER_KEY;

public class ChatRoomActivity extends BaseActivity implements ChatContract.View, ChildEventListener,
        MessageAdapter.Listener, FirebaseUserUtils.RetrieveUserListener, RetrieveMessageLogListener {

    private final String TAG = ChatRoomActivity.class.getSimpleName();
    @BindView(R.id.chat_input_et)
    EditText editText;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private UserModel user;
    private FirebaseMessageUtils firebaseMessageUtils = new FirebaseMessageUtils();
    private String userID;
    private MessageAdapter adapter;
    private ArrayList<MessageModel> messageLog;
    private ChatContract.UserActionListener presenter;
    private FirebaseUserUtils firebaseUserUtils = new FirebaseUserUtils();
    private SharedPrefsUtils sharedPrefsUtils;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    // [END declare_auth]

    public static Intent provideIntent(Context context, String userID) {
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(SENDER_ID_KEY, userID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        initializePresenter();
        setupAdapter();
        setupList();

        firebaseMessageUtils.addMessageChildEventListener(this);
        firebaseUserUtils.addUserEventListener(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (!isNull(userID)) {
            firebaseUserUtils.queryUserByID(userID, this);
        }
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        checkIfUserIsLoggedIn();
    }
    // [END on_start_check_user]

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
        CURRENT_USER_KEY = userID;
        firebaseUserUtils.queryUserByID(userID, this);
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
        firebaseMessageUtils.retrieveMessagesFromFirebase(this);
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
        messageLog = refreshedMessageLog;
        refreshMessageRecyclerView(messageLog);
    }

    public void refreshMessageRecyclerView(ArrayList<MessageModel> messageLog) {
        MessageAdapter adapter = (MessageAdapter) recyclerView.getAdapter();
        adapter.setData(messageLog);
    }

    @Override
    public void sendMessage() {
        String message = editText.getText().toString();
        firebaseMessageUtils.addMessageToFirebaseDb(new MessageModel(message, user.getId(), user.getUsername()));
        clearText(editText, this);
    }

    @Override
    public void fireBaseOnChildAdded(DataSnapshot dataSnapshot, String s) {
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        for (DataSnapshot child : children) {
            MessageModel messageModel = child.getValue(MessageModel.class);
            messageLog.add(messageModel);
            recyclerView.scrollToPosition(messageLog.size() - 1);
            adapter.notifyItemInserted(messageLog.size() - 1);
        }
    }

    @Override
    public void fireBaseOnChildChanged() {
        firebaseMessageUtils.retrieveMessagesFromFirebase(this);
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
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        presenter.childAdded(dataSnapshot, s);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        presenter.childChanged(dataSnapshot, s);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
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

    @Override
    public void signOut() {
        mAuth.signOut();
        updateUI(null);
    }
}
