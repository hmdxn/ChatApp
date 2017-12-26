package com.tronography.locationchat.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.tronography.locationchat.R;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.v4.app.ActivityCompat.checkSelfPermission;
import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * <p/>
 * Contains commonly used methods in an Android App
 */
public class AppUtils {

    public AppUtils() {
    }

    /**
     * Description : Check if user is online or not
     *
     * @return true if online else false
     */

    public static boolean isOnline(Context context) {
        boolean connected = false;
        ConnectivityManager conectivtyManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conectivtyManager != null) {
            connected = conectivtyManager.getActiveNetworkInfo() != null
                    && conectivtyManager.getActiveNetworkInfo().isAvailable()
                    && conectivtyManager.getActiveNetworkInfo().isConnected();
        }

        return connected;
    }

    public static boolean checkPermissions(Activity activity) {
        return checkSelfPermission(activity, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && checkSelfPermission(activity, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED
                && checkSelfPermission(activity, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Description: Clears text from the EditText input field
     *
     * @param view : pass the EditText
     */
    public static void clearText(EditText view) {
        view.setText("");
    }

    /**
     * Show snackbar
     *
     * @param view view clicked
     * @param text text to be displayed on snackbar
     */
    public static void showSnackBar(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Show snackbar
     *
     * @param text text to be displayed on Toast
     */
    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, 9000)
                        .show();
            } else {
                showToast(activity,
                        activity.getApplicationContext()
                                .getResources()
                                .getString(R.string.warning_play_services));
            }
            return false;
        }
        return true;
    }

    /**
     * check if user has enabled Gps of device
     *
     * @return true or false depending upon device Gps status
     */
    public static boolean isGpsEnabled(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Redirect user to enable GPS
     */
    public void goToGpsSettings(Context context) {
        Intent callGPSSettingIntent = new Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(callGPSSettingIntent);
    }
}
