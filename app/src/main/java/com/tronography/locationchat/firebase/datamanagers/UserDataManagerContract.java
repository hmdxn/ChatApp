package com.tronography.locationchat.firebase.datamanagers;

import com.tronography.locationchat.listeners.RetrieveUserListener;
import com.tronography.locationchat.model.User;

/**
 * Created by jonathancolon on 8/26/17.
 */

interface UserDataManagerContract {

    void saveUser(User userObject);

    void updateBio(User userObject, String bio);

    void queryUserByID(final String userId, final RetrieveUserListener retrieveUserListener);

    void updateUsername(User userObject, String newUsername);

}
