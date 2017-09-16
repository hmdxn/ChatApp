package com.tronography.locationchat.database;

import com.tronography.locationchat.listeners.RetrieveUserListener;
import com.tronography.locationchat.model.UserModel;

/**
 * Created by jonathancolon on 8/26/17.
 */

interface UserDaoContract {

    void saveUser(UserModel userObject);

    void updateBio(UserModel userObject, String bio);

    void queryUserByID(final String userId, final RetrieveUserListener retrieveUserListener);

    void updateUsername(UserModel userObject, String newUsername);

}
