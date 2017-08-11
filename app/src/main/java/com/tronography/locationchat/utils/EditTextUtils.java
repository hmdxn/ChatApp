package com.tronography.locationchat.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by jonathancolon on 8/2/17.
 */

public class EditTextUtils {

    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void clearText(EditText view, Activity activity) {
        view.setText("");
        view.clearFocus();
        hideSoftKeyboard(activity);
    }
}
