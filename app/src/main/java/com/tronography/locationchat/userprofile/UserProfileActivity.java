package com.tronography.locationchat.userprofile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tronography.locationchat.ChatApplication;
import com.tronography.locationchat.R;
import com.tronography.locationchat.listeners.RetrieveUserListener;
import com.tronography.locationchat.login.LoginActivity;
import com.tronography.locationchat.model.User;
import com.tronography.locationchat.utils.AppUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tronography.locationchat.utils.Constants.ResultCodes.RESULT_LOAD_BACKGROUND_IMG;
import static com.tronography.locationchat.utils.Constants.ResultCodes.RESULT_LOAD_PROFILE_IMG;
import static com.tronography.locationchat.utils.SharedPrefsUtils.CURRENT_USER_ID;


public class UserProfileActivity extends AppCompatActivity implements UserProfile.View,
        RetrieveUserListener {

    private static final String TAG = UserProfileActivity.class.getSimpleName();
    public static final String BACKGROUND_IMG = "_background_img";
    public static final String PROFILE_IMG = "_profile_img";
    public static final String FIREBASE_URL_REF = "gs://locationchat-27912.appspot.com";
    public final static String SENDER_ID_KEY = "sender_id";

    @BindView(R.id.header_username_tv)
    TextView headerUsernameTV;
    @BindView(R.id.edit_option)
    TextView editOptionTV;
    @BindView(R.id.details_bio_et)
    EditText bioDetailsET;
    @BindView(R.id.details_location_et)
    EditText locationDetailsET;
    @BindView(R.id.profile_iv)
    ImageView profileImage;

    @BindView(R.id.background_iv)
    ImageView backgroundImage;

    private User user;

    @Inject
    AppUtils appUtils;

    @Inject
    public UserProfilePresenter presenter;
    private ProgressDialog progressDialog;

    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl(FIREBASE_URL_REF);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        ((ChatApplication) getApplicationContext()).getAppComponent().inject(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading....");

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
                    headerUsernameTV.getText().toString().trim(),
                    bioDetailsET.getText().toString().trim());
        }
    }

    @OnClick(R.id.profile_iv)
    public void profileImageClicked() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(galleryIntent,
                RESULT_LOAD_PROFILE_IMG);
    }

    @OnClick(R.id.background_iv)
    public void backgroundImageClicked() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(galleryIntent,
                RESULT_LOAD_BACKGROUND_IMG);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case RESULT_LOAD_PROFILE_IMG:
                        uploadProfileImage(data);
                        break;
                    case RESULT_LOAD_BACKGROUND_IMG:
                        uploadBackgroundImage(data);
                        break;
                }
            } else {
                appUtils.showSnackBar(profileImage, getString(R.string.error_message_photo_not_selected));
            }
        } catch (Exception e) {
            appUtils.showSnackBar(profileImage, e.getMessage());
        }
    }

    public void uploadProfileImage(Intent data) {
        Uri profileImageUri = data.getData();

        if (profileImageUri != null) {
            progressDialog.show();

            StorageReference childRef = storageRef.child(CURRENT_USER_ID + PROFILE_IMG);
            //uploading the image
            UploadTask uploadTask = childRef.putFile(profileImageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                progressDialog.dismiss();

                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                if (downloadUrl != null) {
                    presenter.saveProfilePhoto(downloadUrl.toString());
                }

                loadProfilePhoto(downloadUrl);


                Toast.makeText(UserProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(UserProfileActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(UserProfileActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadBackgroundImage(Intent data) {
        Uri backgroundImageUri = data.getData();

        if (backgroundImageUri != null) {
            progressDialog.show();

            //creating a unique identifier
            StorageReference childRef = storageRef.child(CURRENT_USER_ID + BACKGROUND_IMG);
            UploadTask uploadTask = childRef.putFile(backgroundImageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                progressDialog.dismiss();

                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                if (downloadUrl != null) {
                    presenter.saveBackgroundPhoto(downloadUrl.toString());
                }

                loadBackgroundPhoto(downloadUrl);
                Toast.makeText(UserProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {

                progressDialog.dismiss();
                Toast.makeText(UserProfileActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(UserProfileActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfilePhoto(Uri downloadUrl) {
        Glide.with(UserProfileActivity.this)
                .load(downloadUrl)
                .into(profileImage);
    }

    private void loadBackgroundPhoto(Uri downloadUrl) {
        Glide.with(UserProfileActivity.this)
                .load(downloadUrl)
                .into(backgroundImage);
    }

    public void loadBackgroundPhoto(String downloadUrl) {
        Glide.with(UserProfileActivity.this)
                .load(downloadUrl)
                .into(backgroundImage);
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
    public void loadProfilePhoto(String uri) {
        Glide.with(UserProfileActivity.this)
                .load(uri)
                .into(profileImage);
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
