package com.tronography.locationchat.common;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import com.tronography.locationchat.R;


public class BaseActivity extends AppCompatActivity {

    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
