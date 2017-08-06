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

import com.tronography.locationchat.MyAdapter;
import com.tronography.locationchat.R;
import com.tronography.locationchat.model.MessageModel;
import com.tronography.locationchat.model.UserModel;
import com.tronography.locationchat.utils.FirebaseUtils;
import com.tronography.locationchat.utils.UsernameGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tronography.locationchat.utils.EditTextUtils.clearEditText;
import static com.tronography.locationchat.utils.SharedPrefsUtils.configurePrefs;
import static com.tronography.locationchat.utils.SharedPrefsUtils.getPrefsData;

public class ChatRoomActivity extends AppCompatActivity implements MyAdapter.Listener {
    private final static String TAG = ChatRoomActivity.class.getSimpleName();
    private static final String HAS_USERNAME_KEY = "has_username";
    private static final String USERNAME_KEY = "saved_username";
    private static final String USER_ID_KEY = "saved_user_id";
    public static String MY_USER_KEY;
    @BindView(R.id.chat_input_et)
    EditText editText;
    RecyclerView recyclerView;
    String username;
    UserModel user;

    private FirebaseUtils firebaseUtils = new FirebaseUtils();;
    private UsernameGenerator usernameGenerator = new UsernameGenerator();
    private String userID;
    public static final String SENDER_ID_KEY = "sender_id";
    public static final String SENDER_USERNAME_KEY = "sender_username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initRecyclerview();

        SharedPreferences prefs = this.getSharedPreferences(
                "prefs", Context.MODE_PRIVATE);

        if (hasUsername(prefs)) {
            username = getPrefsData(prefs, USERNAME_KEY, "name_not_found", this);
            userID = getPrefsData(prefs, USER_ID_KEY, null, this);
            configureReturningUser(userID, username);
        } else {
            configureNewUser(usernameGenerator.generateTempUsername());
            configurePrefs(prefs, HAS_USERNAME_KEY, true, this);
            configurePrefs(prefs, USERNAME_KEY, user.getUsername(), this);
            configurePrefs(prefs, USER_ID_KEY, user.getId(), this);
        }

        firebaseUtils.addMessageRefChildEventListener(this);
        firebaseUtils.addUserRefChildEventListener();
        firebaseUtils.addUserToFirebase(user);
    }

    private void initRecyclerview() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        MyAdapter adapter = new MyAdapter(this);
        adapter.setData(firebaseUtils.getChatLog());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private boolean hasUsername(SharedPreferences prefs) {
        boolean hasUsername = getPrefsData(prefs, HAS_USERNAME_KEY, false, this);
        return hasUsername;
    }

    private void configureReturningUser(String userID, String username) {
        user = new UserModel(userID, username, null);
        MY_USER_KEY = userID;
    }

    private void configureNewUser(String username) {
        user = new UserModel(username);
        MY_USER_KEY = user.getId();
        this.username = user.getUsername();
    }

    public void refreshMessageLog() {
        MyAdapter adapter = (MyAdapter) recyclerView.getAdapter();
        adapter.setData(firebaseUtils.getChatLog());
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.send_button_iv)
    public void onSendButtonClicked() {
        String message = editText.getText().toString();
        MessageModel messageModel = new MessageModel(message, MY_USER_KEY, username);
        clearEditText(editText, this);
        refreshMessageLog();
        firebaseUtils.addMessageToFirebaseDb(messageModel, user);
    }

    @Override
    public void onMessageLongClicked(MessageModel message) {
        String key = message.getSenderId();
        if (firebaseUtils.getUserMap().containsKey(key)) {
            String id = firebaseUtils.getUserMap().get(key).getId();
            Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMessageClicked(MessageModel message) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(SENDER_ID_KEY, message.getSenderId());
            intent.putExtra(SENDER_USERNAME_KEY, message.getUsername());
            this.startActivity(intent);
    }
}
