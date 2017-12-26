package com.tronography.locationchat.userprofile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.tronography.locationchat.firebase.datamangers.MessageDataManager;
import com.tronography.locationchat.firebase.datamangers.UserDataManager;
import com.tronography.locationchat.listeners.RetrieveMessageLogListener;
import com.tronography.locationchat.listeners.RetrieveUserListener;
import com.tronography.locationchat.model.Message;
import com.tronography.locationchat.model.User;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.tronography.locationchat.utils.SharedPrefsUtils.CURRENT_USER_ID;


public class UserProfilePresenter implements RetrieveMessageLogListener,
        RetrieveUserListener {

    private UserProfile.View view;
    private User user;
    private String mUserId;
    private String mSenderId;
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
        mMessageDataManager.updateSenderName(user);
        firebaseUser.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build());
        mUserDataManager.updateUsername(user,username);
        mUserDataManager.updateBio(user, bio);
    }

    void verifyUserAuth() {
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            mUserId = firebaseUser.getUid();
        } else {
            view.launchLoginActivity();
        }
    }

    @Override
    public void onUserRetrieved(User user) {
        this.user = user;
        System.out.println("user = " + this.user.toString());
        if (user.getProfilePhoto() != null){
            view.loadProfilePhoto(user.getProfilePhoto());
        }

        if (user.getBackgroundPhoto() != null){
            view.loadBackgroundPhoto(user.getBackgroundPhoto());
        }
        view.setUsernameText(this.user.getUsername());
        view.setBioText(this.user.getBio());
        if (mSenderId.equals(CURRENT_USER_ID)){
            view.showEditOption();
        }
    }

    public void saveProfilePhoto(String uri){
        mUserDataManager.updateProfilePhoto(user, uri);
    }

    public void saveBackgroundPhoto(String uri){
        mUserDataManager.updateBackgroundPhoto(user, uri);
    }

    @Override
    public void onMessageLogReceived(ArrayList<Message> messageLog) {

    }

    public void queryUserById(String senderId) {
        mSenderId = senderId;
        mUserDataManager.queryUserByID(mSenderId, this);
    }
}
