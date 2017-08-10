package com.tronography.locationchat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.tronography.locationchat.model.UserModel;

/**
 * Created by jonathancolon on 8/3/17.
 */

public class SharedPrefsUtils {

    public static final String HAS_USERNAME_KEY = "has_username";
    public static final String USERNAME_KEY = "saved_username";
    public static final String USER_ID_KEY = "saved_user_id";
    public static String MY_USER_KEY;
    public static String MY_USER_NAME;

    public static boolean getPrefsData(SharedPreferences prefs, String key, @Nullable boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }

    public static String getPrefsData(SharedPreferences prefs, String key, @Nullable String defValue) {
        return prefs.getString(key, defValue);
    }

    public static void configurePrefs(SharedPreferences prefs, String key, Boolean defValue) {
        prefs.edit()
                .putBoolean(key, defValue)
                .apply();
    }

    public static void configurePrefs(SharedPreferences prefs, String key, String defValue) {
        prefs.edit()
                .putString(key, defValue)
                .apply();
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    public static void updateUserDetails(SharedPreferences prefs, UserModel userModel) {
        updateUsername(prefs, userModel);
        updateUserId(prefs, userModel);
    }

    public static void updateUserId(SharedPreferences prefs, UserModel userModel) {
        configurePrefs(prefs, SharedPrefsUtils.USERNAME_KEY, userModel.getUsername());
        configurePrefs(prefs, USER_ID_KEY, userModel.getId());
    }

    public static void updateUsername(SharedPreferences prefs, UserModel userModel) {
        configurePrefs(prefs, SharedPrefsUtils.USERNAME_KEY, userModel.getUsername());
        configurePrefs(prefs, USER_ID_KEY, userModel.getId());
    }


}
