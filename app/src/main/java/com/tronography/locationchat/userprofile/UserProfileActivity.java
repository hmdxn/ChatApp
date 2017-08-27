package com.tronography.locationchat.userprofile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tronography.locationchat.BaseActivity;
import com.tronography.locationchat.R;
import com.tronography.locationchat.database.MessageDoa;
import com.tronography.locationchat.database.UserDao;
import com.tronography.locationchat.listeners.RetrieveUserListener;
import com.tronography.locationchat.model.UserModel;
import com.tronography.locationchat.utils.SharedPrefsUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tronography.locationchat.utils.SharedPrefsUtils.CURRENT_USER_KEY;

public class UserProfileActivity extends BaseActivity implements RetrieveUserListener {

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
    private UserDao userDao;
    MessageDoa messageDoa;

    public final static String SENDER_ID_KEY = "sender_id";
    private SharedPrefsUtils sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);

        sharedPrefs = new SharedPrefsUtils(this);
        userDao = new UserDao();
        messageDoa = new MessageDoa();

        String userId = getIntent().getStringExtra(SENDER_ID_KEY);
        userDao.queryUserByID(userId, this);
    }

    @OnClick(R.id.edit_option)
    public void onClick() {

        if (editOptionTV.getText().equals("EDIT")){
            editOptionTV.setText(R.string.save);
            enableDetailsEditText();
        } else if (editOptionTV.getText().equals("SAVE")){
            editOptionTV.setText(R.string.edit);
            disableDetailsEditText();
            saveChanges();
        }
    }

    private void saveChanges() {
        messageDoa.updateSenderName(userModel);
        userDao.updateUsername(userModel, headerUsernameTV.getText().toString());
        userDao.updateBio(userModel, bioDetailsET.getText().toString());
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
        bioDetailsET.setText(userModel.getBio());
        if (CURRENT_USER_KEY.equals(userModel.getId())){
            editOptionTV.setVisibility(View.VISIBLE);
        }
    }

    public static Intent provideIntent(Context context, String userName) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(SENDER_ID_KEY, userName);
        return intent;
    }
}
