package com.tronography.locationchat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/**
 * Created by jonathancolon on 8/3/17.
 */

public class SharedPrefsUtils {

    public static boolean getPrefsData(SharedPreferences prefs, String key, @Nullable boolean defValue, Context context) {
        return prefs.getBoolean(key, defValue);
    }

    public static String getPrefsData(SharedPreferences prefs, String key, @Nullable String defValue, Context context) {
        return prefs.getString(key, defValue);
    }

    public static void configurePrefs(SharedPreferences prefs, String key, Boolean defValue, Context context) {
        prefs.edit()
                .putBoolean(key, defValue)
                .apply();
    }

    public static void configurePrefs(SharedPreferences prefs, String key, String defValue, Context context) {
        prefs.edit()
                .putString(key, defValue)
                .apply();
    }
}
