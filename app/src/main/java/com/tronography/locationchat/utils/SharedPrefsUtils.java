package com.tronography.locationchat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.tronography.locationchat.model.User;

/**
 * Created by jonathancolon on 8/3/17.
 */

public class SharedPrefsUtils {
    public static String CURRENT_USER_ID;
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

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }


}
