package com.tronography.locationchat.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.tronography.locationchat.R;
import com.tronography.locationchat.common.BaseActivity;
import com.tronography.locationchat.firebase.datamangers.UserDataManager;
import com.tronography.locationchat.listeners.RetrieveUserListener;
import com.tronography.locationchat.lobby.LobbyActivity;
import com.tronography.locationchat.model.User;
import com.tronography.locationchat.utils.UsernameGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tronography.locationchat.utils.SharedPrefsUtils.CURRENT_USER_ID;


public class LoginActivity extends BaseActivity implements View.OnClickListener,
        RetrieveUserListener {

    private static final String TAG = "EmailPassword";
    private RetrieveUserListener retrieveUserListener;

    @BindView(R.id.status)
    TextView authStatusTv;

    @BindView(R.id.detail)
    TextView detailTv;

    @BindView(R.id.email_field)
    EditText emailField;

    @BindView(R.id.password_field)
    EditText passwordField;

    private UserDataManager userDataManager;
    private UsernameGenerator usernameGenerator;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        retrieveUserListener = this;
        userDataManager = new UserDataManager(this);

        checkPermissions();

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        updateUI(firebaseUser);
    }

    private void updateUI(FirebaseUser user) {
        Log.e(TAG, "updateUI: " + "CALLED");
        hideProgressDialog();
        if (user != null) {
            authStatusTv.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            detailTv.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            CURRENT_USER_ID = firebaseUser.getUid();

            launchLobbyActivity(this);
        } else {
            authStatusTv.setText(R.string.signed_out);
            detailTv.setText(null);
        }
    }

    public void launchLobbyActivity(Context context) {
        Log.i(TAG, "launchLobbyActivity: called");
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

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            firebaseUser = firebaseAuth.getCurrentUser();
                            updateUI(firebaseUser);
                            usernameGenerator = new UsernameGenerator();

                            configureNewUser(usernameGenerator.generateTempUsername(),
                                    firebaseUser.getUid());
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
        CURRENT_USER_ID = user.getId();
        userDataManager.saveUser(user);
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            firebaseUser = firebaseAuth.getCurrentUser();
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
                            authStatusTv.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(emailField.getText().toString(), passwordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(emailField.getText().toString(), passwordField.getText().toString());
        }
    }

    @Override
    public void onUserRetrieved(User user) {
        Toast.makeText(this, user.getUsername() + " signed in", Toast.LENGTH_SHORT).show();
        CURRENT_USER_ID = user.getId();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
