package com.tronography.locationchat.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.tronography.locationchat.ChatApplication;
import com.tronography.locationchat.common.BaseActivity;
import com.tronography.locationchat.R;
import com.tronography.locationchat.firebase.datamanagers.UserDataManager;
import com.tronography.locationchat.listeners.RetrieveUserListener;
import com.tronography.locationchat.lobby.LobbyActivity;
import com.tronography.locationchat.model.User;
import com.tronography.locationchat.utils.SharedPrefsUtils;
import com.tronography.locationchat.utils.UsernameGenerator;

import butterknife.Bind;

import static com.tronography.locationchat.utils.SharedPrefsUtils.CURRENT_USER_KEY;


public class LoginActivity extends BaseActivity implements View.OnClickListener,
        RetrieveUserListener {

    private static final String TAG = "EmailPassword";
    private RetrieveUserListener retrieveUserListener;

    @Bind(R.id.status)
    TextView mStatusTextView;

    @Bind(R.id.detail)
    TextView mDetailTextView;

    @Bind(R.id.field_email)
    EditText mEmailField;

    @Bind(R.id.field_password)
    EditText mPasswordField;

    private UserDataManager userDataManager;
    private UsernameGenerator usernameGenerator;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        retrieveUserListener = this;
        userDataManager = new UserDataManager(this);

        mStatusTextView = (TextView) findViewById(R.id.status);
        mDetailTextView = (TextView) findViewById(R.id.detail);
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseUser = mAuth.getCurrentUser();
        updateUI(firebaseUser);
    }

    private void updateUI(FirebaseUser user) {
        Log.e(TAG, "updateUI: " + "CALLED" );
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());
            launchLobbyActivity(this);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
        }
    }

    public void launchLobbyActivity(Context context) {
        Log.e(TAG, "launchLobbyActivity: called");
        Intent intent = LobbyActivity.provideIntent(context);
        startActivity(intent);
        finish();
    }

    public static Intent provideIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            firebaseUser = mAuth.getCurrentUser();
                            updateUI(firebaseUser);
                            usernameGenerator = new UsernameGenerator();

                            configureNewUser(usernameGenerator.generateTempUsername(), firebaseUser.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });

    }

    private void configureNewUser(String username, String uid) {
        User user = new User(username, uid);
        CURRENT_USER_KEY = user.getId();
        userDataManager.saveUser(user);
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            firebaseUser = mAuth.getCurrentUser();
                            updateUI(firebaseUser);
                            userDataManager.queryUserByID(firebaseUser.getUid(), retrieveUserListener);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verify_email_button).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "Failed to addRoomClicked verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification();
        }
    }

    @Override
    public void onUserRetrieved(User user) {
        Toast.makeText(this, user.getUsername() + " signed in", Toast.LENGTH_SHORT).show();
        CURRENT_USER_KEY = user.getId();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
