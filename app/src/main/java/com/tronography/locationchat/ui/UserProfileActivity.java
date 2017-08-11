package com.tronography.locationchat.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tronography.locationchat.R;
import com.tronography.locationchat.firebase.FirebaseUtils;
import com.tronography.locationchat.model.UserModel;
import com.tronography.locationchat.utils.SharedPrefsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfileActivity extends AppCompatActivity implements FirebaseUtils.RetrieveUserListener {

    private static final String TAG = UserProfileActivity.class.getSimpleName();
    @BindView(R.id.header_username_tv)
    TextView headerUsernameTV;
    @BindView(R.id.edit_option)
    TextView editOptionTV;
    @BindView(R.id.details_bio_et)
    EditText bioDetailsET;
    @BindView(R.id.details_location_et)
    EditText locationDetailsET;
    private UserModel userModel;
    FirebaseUtils firebaseUtils = new FirebaseUtils();
    public final String SENDER_ID_KEY = "sender_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);

        String userId = getIntent().getStringExtra(SENDER_ID_KEY);
        firebaseUtils.queryUserByID(userId, this);
    }

    @OnClick(R.id.edit_option)
    public void onClick() {

        if (editOptionTV.getText().equals("EDIT")){
            editOptionTV.setText(R.string.save);
            enableDetailsEditText();
        } else if (editOptionTV.getText().equals("SAVE")){
            editOptionTV.setText(R.string.edit);
            disableDetailsEditText();
            firebaseUtils.applyNewUsernameInFireBase(userModel, headerUsernameTV.getText().toString());
            firebaseUtils.applyBioDetailsInFireBase(userModel, bioDetailsET.getText().toString());
            SharedPreferences prefs = SharedPrefsUtils.getSharedPreferences(this);
            if (SharedPrefsUtils.MY_USER_KEY.equals(userModel.getId())){
                SharedPrefsUtils.updateUsername(prefs, userModel);
            }
            firebaseUtils.updateMessageSenderUsernames(userModel);
        }
    }

    private void enableDetailsEditText() {
        bioDetailsET.setEnabled(true);
        locationDetailsET.setEnabled(true);
        headerUsernameTV.setEnabled(true);
    }

    private void disableDetailsEditText(){
        bioDetailsET.setEnabled(false);
        locationDetailsET.setEnabled(false);
        headerUsernameTV.setEnabled(false);
    }

    @Override
    public void onUserRetrieved(UserModel queriedUser) {
        this.userModel = queriedUser;
        headerUsernameTV.setText(userModel.getUsername());
        SharedPreferences prefs = SharedPrefsUtils.getSharedPreferences(this);
        if (SharedPrefsUtils.MY_USER_KEY.equals(userModel.getId())){
            SharedPrefsUtils.updateUsername(prefs, userModel);
            editOptionTV.setVisibility(View.VISIBLE);
        }
        firebaseUtils.updateMessageSenderUsernames(userModel);
    }

}
