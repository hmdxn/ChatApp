package com.tronography.locationchat.userprofile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.tronography.locationchat.firebase.datamanagers.MessageDataManager;
import com.tronography.locationchat.firebase.datamanagers.UserDataManager;
import com.tronography.locationchat.listeners.RetrieveMessageLogListener;
import com.tronography.locationchat.listeners.RetrieveUserListener;
import com.tronography.locationchat.model.Message;
import com.tronography.locationchat.model.User;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.tronography.locationchat.utils.SharedPrefsUtils.CURRENT_USER_KEY;


public class UserProfilePresenter implements RetrieveMessageLogListener,
        RetrieveUserListener {

    private UserProfile.View view;
    private User mUser;
    private String mUserId;
    private MessageDataManager mMessageDataManager = new MessageDataManager(this);
    private UserDataManager mUserDataManager = new UserDataManager(this);
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;


    @Inject
    public UserProfilePresenter() {
    }

    public void setView(UserProfile.View view) {
        this.view = view;
    }

    void saveChanges(String username, String bio) {
        mMessageDataManager.updateSenderName(mUser);
        firebaseUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(username).build());
        mUserDataManager.updateUsername(mUser,username);
        mUserDataManager.updateBio(mUser, bio);
    }

    void verifyUserAuth() {
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            mUserId = firebaseUser.getUid();
            loadReturningUser(firebaseUser.getUid());
        } else {
            view.launchLoginActivity();
        }
    }

    @Override
    public void onUserRetrieved(User user) {
        mUser = user;
        view.setUsernameText(mUser.getUsername());
        view.setBioText(mUser.getBio());
        if (CURRENT_USER_KEY.equals(mUser.getId())) {
            view.showEditOption();
        }
    }

    private void loadReturningUser(String userId) {
        mUserDataManager.queryUserByID(mUserId, this);
    }

    @Override
    public void onMessageLogReceived(ArrayList<Message> messageLog) {

    }

    public void queryUserById() {
        mUserDataManager.queryUserByID(mUserId, this);
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }
}
