package com.tronography.locationchat.userprofile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static com.tronography.locationchat.utils.Constants.ResultCodes.*;
import static com.tronography.locationchat.utils.Constants.ResultCodes.RESULT_LOAD_BACKGROUND_IMG;
import static com.tronography.locationchat.utils.SharedPrefsUtils.CURRENT_USER_ID;


public class UserProfileActivity extends AppCompatActivity implements UserProfile.View,
        RetrieveUserListener {

    private static final String TAG = UserProfileActivity.class.getSimpleName();

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

    private User user;
    public final static String SENDER_ID_KEY = "sender_id";

    @Inject
    AppUtils appUtils;

    @Inject
    public UserProfilePresenter presenter;
    private ProgressDialog progressDialog;

    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://locationchat-27912.appspot.com");

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
                    headerUsernameTV.getText().toString(),
                    bioDetailsET.getText().toString());
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
                        //todo upload to firebase storage and display in ui
                        break;
                    case RESULT_LOAD_BACKGROUND_IMG:
                        uploadBackGroundImage(data);
                        Uri bgImgUri = data.getData();
                        break;
                }
            } else {
                appUtils.showSnackBar(profileImage, getString(R.string.error_message_photo_not_selected));
            }
        } catch (Exception e) {
            appUtils.showSnackBar(profileImage, e.getMessage());
        }
    }

    public void uploadBackGroundImage(Intent data) {
        Uri bgImgUri = data.getData();

        if(bgImgUri != null) {
            progressDialog.show();

            StorageReference childRef = storageRef.child(CURRENT_USER_ID + "backgroundImage.jpg");
            //uploading the image
            UploadTask uploadTask = childRef.putFile(bgImgUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(UserProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UserProfileActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(UserProfileActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
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
