package com.tronography.locationchat.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.tronography.locationchat.R;
import com.tronography.locationchat.model.UserModel;
import com.tronography.locationchat.utils.FirebaseUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfileActivity extends AppCompatActivity implements FirebaseUtils.Listener {

    UserModel userModel;

    private static final String TAG = UserProfileActivity.class.getSimpleName();
    @BindView(R.id.header_username_tv)
    TextView header_username_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);

        FirebaseUtils firebaseUtils = new FirebaseUtils();
        String userId = getIntent().getStringExtra(ChatRoomActivity.SENDER_ID_KEY);
        String username = getIntent().getStringExtra(ChatRoomActivity.SENDER_USERNAME_KEY);




    }


    @Override
    public void onUserRetrieved(UserModel queriedUser) {
        userModel = queriedUser;
        header_username_tv.setText(userModel.getUsername());
    }
}
