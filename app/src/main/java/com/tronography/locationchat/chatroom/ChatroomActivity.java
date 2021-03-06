package com.tronography.locationchat.chatroom;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tronography.locationchat.ChatApplication;
import com.tronography.locationchat.R;
import com.tronography.locationchat.firebase.eventlisteners.MessageEventListener;
import com.tronography.locationchat.firebase.eventlisteners.UserEventListener;
import com.tronography.locationchat.login.LoginActivity;
import com.tronography.locationchat.model.Message;
import com.tronography.locationchat.userprofile.UserProfileActivity;
import com.tronography.locationchat.utils.AppUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tronography.locationchat.utils.ObjectUtils.isEmpty;

public class ChatroomActivity extends AppCompatActivity implements Chatroom.View,
        ChatroomAdapter.Listener {

    private static final String ROOM_ID_KEY = "room_id";
    private static final String ROOM_NAME_KEY = "room_name";
    private final String TAG = ChatroomActivity.class.getSimpleName();

    @BindView(R.id.chat_input_et)
    EditText editText;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar_chatroom)
    Toolbar toolbar;

    private ChatroomAdapter adapter;

    @Inject
    AppUtils appUtils;

    @Inject
    ChatroomPresenter presenter;
    private String roomName;


    public static Intent provideIntent(Context context, String roomName) {
        Intent intent = new Intent(context, ChatroomActivity.class);
        intent.putExtra(ROOM_NAME_KEY, roomName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        ((ChatApplication) getApplicationContext()).getAppComponent().inject(this);
        ButterKnife.bind(this);

        roomName = getIntent().getStringExtra(ROOM_NAME_KEY);

        presenter.setView(this);
        presenter.setRoomId(roomName);
        presenter.verifyUserAuth();
        presenter.updateUi();
    }


    public void setupToolBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getColor(R.color.light_font));
        }
        setSupportActionBar(toolbar);
        setTitle(" Room : " + roomName);
    }

    @Override
    public void attachFirebaseEventListeners() {
        MessageEventListener messageEventListener = new MessageEventListener();
        messageEventListener.addMessageChildEventListener(presenter, roomName);

        UserEventListener userEventListener = new UserEventListener();
        userEventListener.addUserChildEventListener(presenter);
    }

    @Override
    public void launchLoginActivity() {
        Intent intent = LoginActivity.provideIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void setupAdapter(ArrayList<Message> messageLog) {
        adapter = new ChatroomAdapter(this);
        adapter.setData(messageLog);
    }

    @Override
    public void setupList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onMessageLongClicked(Message message) {
        presenter.messageLongClicked(message);
    }

    @Override
    public void onMessageClicked(Message message) {
        presenter.launchUserProfile(message);
    }

    @OnClick(R.id.chat_input_et)
    public void textFieldClicked() {
        presenter.textFieldClicked();
    }

    @OnClick(R.id.send_button_iv)
    public void submit() {
        String message = editText.getText().toString();
        if (!isEmpty(message)) {
            presenter.sendMessage(message.trim());
            adapter.notifyDataSetChanged();
            appUtils.clearText(editText);
        }
    }

    @Override
    public void refreshMessageRecyclerView(ArrayList<Message> messageLog) {
        ChatroomAdapter adapter = (ChatroomAdapter) recyclerView.getAdapter();
        adapter.setData(messageLog);
    }

    @Override
    public void updateMessageLog(ArrayList<Message> messageLog) {
        Log.i(TAG, "updateMessageLog: " + messageLog.size());
        refreshMessageRecyclerView(messageLog);
    }

    @Override
    public void launchUserProfileActivity(String senderID) {
        Intent intent = UserProfileActivity.provideIntent(this, senderID);
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                presenter.signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void notifyRecyclerViewOfChanges(ArrayList<Message> messageLog) {
        Log.e(TAG, "scrollToBottom: CALLED");
        recyclerView.getAdapter().notifyItemInserted(messageLog.size() - 1);
        recyclerView.scrollToPosition(messageLog.size() - 1);
    }

    @Override
    public void scrollToBottom(final ArrayList<Message> messageLog) {
        Log.e(TAG, "scrollToBottom: CALLED");
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                recyclerView.scrollToPosition(messageLog.size() - 1);
            }
        });
    }
}
