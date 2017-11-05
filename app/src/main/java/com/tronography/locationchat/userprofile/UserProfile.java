package com.tronography.locationchat.userprofile;

/**
 * Created by jonat on 10/30/2017.
 */

public interface UserProfile {

    interface View{

        void enableDetailsEditText();

        void disableDetailsEditText();

        void setBioText(String bio);

        void setUsernameText(String username);

        void showEditOption();

        void launchLoginActivity();
    }

}
