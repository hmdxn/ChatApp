package com.tronography.locationchat.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.tronography.locationchat.ChatContract;
import com.tronography.locationchat.ChatPresenter;
import com.tronography.locationchat.MessageAdapter;
import com.tronography.locationchat.R;
import com.tronography.locationchat.firebase.FirebaseUtils;
import com.tronography.locationchat.model.MessageModel;
import com.tronography.locationchat.model.UserModel;
import com.tronography.locationchat.utils.UsernameGenerator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tronography.locationchat.firebase.FirebaseUtils.RetrieveMessageLogListener;
import static com.tronography.locationchat.firebase.FirebaseUtils.RetrieveUserListener;
import static com.tronography.locationchat.utils.EditTextUtils.clearText;
import static com.tronography.locationchat.utils.ObjectUtils.isNull;
import static com.tronography.locationchat.utils.SharedPrefsUtils.HAS_USERNAME_KEY;
import static com.tronography.locationchat.utils.SharedPrefsUtils.MY_USER_KEY;
import static com.tronography.locationchat.utils.SharedPrefsUtils.USERNAME_KEY;
import static com.tronography.locationchat.utils.SharedPrefsUtils.USER_ID_KEY;
import static com.tronography.locationchat.utils.SharedPrefsUtils.configurePrefs;
import static com.tronography.locationchat.utils.SharedPrefsUtils.getPrefsData;

public class ChatRoomActivity extends AppCompatActivity implements ChatContract.View, ChildEventListener,
        MessageAdapter.Listener, RetrieveUserListener, RetrieveMessageLogListener {

    public final String SENDER_ID_KEY = "sender_id";
    public final String SENDER_USERNAME_KEY = "sender_username";
    private final String TAG = ChatRoomActivity.class.getSimpleName();
    @BindView(R.id.chat_input_et)
    EditText editText;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private String username;
    private SharedPreferences prefs;
    private UserModel user;
    private FirebaseUtils firebaseUtils = new FirebaseUtils();
    private UsernameGenerator usernameGenerator = new UsernameGenerator();
    private String userID;
    private MessageAdapter adapter;
    private ArrayList<MessageModel> messageLog;
    private ChatContract.UserActionListener presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initializePresenter();
        setupAdapter();
        setupList();

        retrieveUserDetailsFromSharedPreferences();
        firebaseUtils.addMessageChildEventListener(this);
        firebaseUtils.addUserEventListener(this);

        if (!isNull(userID)) {
            firebaseUtils.queryUserByID(userID, this);
        }
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

    private void retrieveUserDetailsFromSharedPreferences() {
        prefs = this.getSharedPreferences(
                "prefs", Context.MODE_PRIVATE);

        if (hasUsername(prefs)) {
            username = getPrefsData(prefs, USERNAME_KEY, null);
            userID = getPrefsData(prefs, USER_ID_KEY, null);
            firebaseUtils.queryUserByID(userID, this);
        } else {
            configureNewUser(usernameGenerator.generateTempUsername());
            configurePrefs(prefs, HAS_USERNAME_KEY, true);
            configurePrefs(prefs, USERNAME_KEY, user.getUsername());
            configurePrefs(prefs, USER_ID_KEY, user.getId());
            firebaseUtils.addUserToFirebase(user);
        }
    }

    private boolean hasUsername(SharedPreferences prefs) {
        return getPrefsData(prefs, HAS_USERNAME_KEY, false);
    }

    private void configureNewUser(String username) {
        user = new UserModel(username);
        MY_USER_KEY = user.getId();
        this.username = user.getUsername();
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveUserDetailsFromSharedPreferences();
        firebaseUtils.retrieveMessagesFromFirebase(this);
    }

    @Override
    public void onMessageLongClicked(MessageModel message) {
        Toast.makeText(this, message.getSenderId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessageClicked(MessageModel message) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(SENDER_ID_KEY, message.getSenderId());
        intent.putExtra(SENDER_USERNAME_KEY, message.getUsername());
        this.startActivity(intent);
    }

    @OnClick(R.id.send_button_iv)
    public void submit() {
        presenter.send();
    }

    @Override
    public void onUserRetrieved(UserModel userModel) {
        user = userModel;
        userID = user.getId();
        applyUserDetails(prefs, userModel);
    }

    void applyUserDetails(SharedPreferences prefs, UserModel userModel) {
        configureReturningUser(userID, userModel.getUsername());
        configurePrefs(prefs, USERNAME_KEY, user.getUsername());
        firebaseUtils.addUserToFirebase(user);
    }

    private void configureReturningUser(String userID, String username) {
        user = new UserModel(userID, username, null);
        MY_USER_KEY = userID;
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
        firebaseUtils.addMessageToFirebaseDb(new MessageModel(message, user.getId(), user.getUsername()));
        clearText(editText, this);
    }

    @Override
    public void fireBaseOnChildAdded(DataSnapshot dataSnapshot, String s) {
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();

        for (DataSnapshot child : children) {
            MessageModel messageModel = child.getValue(MessageModel.class);
            messageLog.add(messageModel);
            adapter.notifyItemInserted(messageLog.size() - 1);
        }
    }

    @Override
    public void fireBaseOnChildChanged() {
        firebaseUtils.retrieveMessagesFromFirebase(this);
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
}
