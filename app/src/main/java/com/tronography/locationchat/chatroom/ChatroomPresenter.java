package com.tronography.locationchat.chatroom;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.tronography.locationchat.firebase.datamangers.MessageDataManager;
import com.tronography.locationchat.firebase.datamangers.UserDataManager;
import com.tronography.locationchat.listeners.RetrieveMessageLogListener;
import com.tronography.locationchat.listeners.RetrieveUserListener;
import com.tronography.locationchat.model.Message;
import com.tronography.locationchat.model.User;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.tronography.locationchat.utils.ObjectUtils.isNull;


public class ChatroomPresenter implements RetrieveMessageLogListener,
        RetrieveUserListener {

    private String mUserId;
    private Chatroom.View view;
    private MessageDataManager mMessageDataManager = new MessageDataManager(this);
    private UserDataManager mUserDataManager = new UserDataManager(this);
    private String mRoomId;
    private User mUser;
    private FirebaseAuth mAuth;
    private ArrayList<Message> messageLog = new ArrayList<>();
    private static final String TAG = ChatroomPresenter.class.getSimpleName();


    @Inject
    public ChatroomPresenter() {
    }

    void sendMessage(String message) {
        Message messageModel = new Message(message, mUser.getId(), mUser.getUsername());
        mMessageDataManager.saveMessage(messageModel, mRoomId);
    }

    public void onMessageAdded(Message message) {
        messageLog.add(message);
        view.notifyRecyclerViewOfChanges(messageLog);
    }

    void setRoomId(String roomId) {
        mRoomId = roomId;
    }

    public void onMessageLogChanged(DataSnapshot dataSnapshot, String s) {
        mMessageDataManager.getMessageLog(mRoomId);
    }

    void launchUserProfile(Message message) {
        if ((!isNull(message.getSenderId()))) {
            view.launchUserProfileActivity(message.getSenderId());
        }
    }

    void messageLongClicked(Message message) {
        view.showMessage(message.getMessageId());
    }

    void signOut() {
        mAuth.signOut();
        view.launchLoginActivity();
    }

    @Override
    public void onMessageLogReceived(ArrayList<Message> messageLog) {
        view.hideProgressBar();
        view.updateMessageLog(messageLog);
        view.scrollToBottom(messageLog);
    }

    @Override
    public void onUserRetrieved(User user) {
        Log.i(TAG, "onUserRetrieved: " + user.getUsername() + " retrieved");
        mUser = user;
    }

    void updateUi() {
        view.setupAdapter(messageLog);
        view.setupList();
        view.setupToolBar();
        view.attachFirebaseEventListeners();
    }

    void verifyUserAuth() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            mUserId = user.getUid();
            loadReturningUser(user.getUid());
        } else {
            view.launchLoginActivity();
        }
    }

    void textFieldClicked() {
        view.scrollToBottom(messageLog);
    }

    public void setView(Chatroom.View view) {
        this.view = view;
    }

    private void loadReturningUser(String userId) {
        mUserDataManager.queryUserByID(mUserId, this);
    }
}
