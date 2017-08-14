package com.tronography.locationchat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.tronography.locationchat.model.UserModel;

/**
 * Created by jonathancolon on 8/3/17.
 */

public class SharedPrefsUtils {

    public static final String HAS_USER_ID = "has_sender_id";
    public static final String USERNAME_KEY = "saved_username";
    public static final String USER_ID_KEY = "saved_user_id";
    public static String CURRENT_USER_KEY;
    private SharedPreferences prefs;

    public SharedPrefsUtils(Context context) {
        prefs = getSharedPreferences(context);
    }

    private boolean getPrefsData(String key, @Nullable boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }

    private String getPrefsData(String key, @Nullable String defValue) {
        return prefs.getString(key, defValue);
    }

    private void setSharedPreferencesData(SharedPreferences prefs, String key, Boolean defValue) {
        prefs.edit()
                .putBoolean(key, defValue)
                .apply();
    }

    private void setSharedPreferencesData(SharedPreferences prefs, String key, String defValue) {
        prefs.edit()
                .putString(key, defValue)
                .apply();
    }

    public boolean getHasSenderId(){
       return getPrefsData(HAS_USER_ID, false);
    }

    public String getReturningUserId(){
        return getPrefsData(USER_ID_KEY, null);
    }


    public void setSharedPreferencesUserId(String userId){
        setSharedPreferencesData(prefs, USER_ID_KEY, userId);
    }

    public void setHasSenderId(Boolean hasSenderId){
        setSharedPreferencesData(prefs, HAS_USER_ID, true);
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    public void updateUserDetails(UserModel userModel) {
        updateUsername(userModel);
        updateUserId(userModel);
    }

    public void updateUserId(UserModel userModel) {
        setSharedPreferencesData(prefs, SharedPrefsUtils.USERNAME_KEY, userModel.getUsername());
        setSharedPreferencesData(prefs, USER_ID_KEY, userModel.getId());
    }

    public void updateUsername(UserModel userModel) {
        setSharedPreferencesData(prefs, SharedPrefsUtils.USERNAME_KEY, userModel.getUsername());
        setSharedPreferencesData(prefs, USER_ID_KEY, userModel.getId());
    }


}
