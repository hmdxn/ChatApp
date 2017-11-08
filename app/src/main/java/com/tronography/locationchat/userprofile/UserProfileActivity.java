package com.tronography.locationchat.userprofile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tronography.locationchat.ChatApplication;
import com.tronography.locationchat.R;
import com.tronography.locationchat.listeners.RetrieveUserListener;
import com.tronography.locationchat.login.LoginActivity;
import com.tronography.locationchat.model.User;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tronography.locationchat.utils.SharedPrefsUtils.CURRENT_USER_ID;


public class UserProfileActivity extends AppCompatActivity implements UserProfile.View,
        RetrieveUserListener {

    private static final String TAG = UserProfileActivity.class.getSimpleName();

    @Bind(R.id.header_username_tv)
    TextView headerUsernameTV;
    @Bind(R.id.edit_option)
    TextView editOptionTV;
    @Bind(R.id.details_bio_et)
    EditText bioDetailsET;
    @Bind(R.id.details_location_et)
    EditText locationDetailsET;
    private User user;
    public final static String SENDER_ID_KEY = "sender_id";

    @Inject
    public UserProfilePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        ((ChatApplication) getApplicationContext()).getAppComponent().inject(this);

        presenter.setView(this);
        presenter.verifyUserAuth();
        presenter.queryUserById(getIntent().getStringExtra(SENDER_ID_KEY));
    }

    @OnClick(R.id.edit_option)
    public void onClick() {
        if (isEditDisabled()) {
            showSaveButton();
            enableDetailsEditText();
        } else if (isEditEnabled()) {
            showEditButton();
            disableDetailsEditText();
            presenter.saveChanges(
                    headerUsernameTV.getText().toString(),
                    bioDetailsET.getText().toString());
        }
    }

    private void showEditButton() {
        editOptionTV.setText(R.string.edit);
    }

    private void showSaveButton() {
        editOptionTV.setText(R.string.save);
    }

    private boolean isEditEnabled() {
        return editOptionTV.getText().equals(getString(R.string.save));
    }

    private boolean isEditDisabled() {
        return editOptionTV.getText().equals(getString(R.string.edit));
    }


    @Override
    public void enableDetailsEditText() {
        bioDetailsET.setEnabled(true);
        locationDetailsET.setEnabled(true);
        headerUsernameTV.setEnabled(true);
    }

    @Override
    public void disableDetailsEditText() {
        bioDetailsET.setEnabled(false);
        locationDetailsET.setEnabled(false);
        headerUsernameTV.setEnabled(false);
    }

    @Override
    public void onUserRetrieved(User queriedUser) {
        this.user = queriedUser;
        if (CURRENT_USER_ID.equals(user.getId())) {
            showEditOption();
        }
    }

    @Override
    public void setBioText(String bio) {
        bioDetailsET.setText(bio);
    }

    @Override
    public void setUsernameText(String username) {
        headerUsernameTV.setText(username);
    }

    @Override
    public void showEditOption() {
        editOptionTV.setVisibility(View.VISIBLE);
    }

    public static Intent provideIntent(Context context, String userName) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(SENDER_ID_KEY, userName);
        return intent;
    }

    @Override
    public void launchLoginActivity() {
        Intent intent = LoginActivity.provideIntent(this);
        startActivity(intent);
        finish();
    }
}
