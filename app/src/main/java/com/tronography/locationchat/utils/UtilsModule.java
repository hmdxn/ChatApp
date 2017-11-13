package com.tronography.locationchat.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Module
public class UtilsModule {

    private Context mContext;

    public UtilsModule(Context context) {
        this.mContext = context;
    }

    // get AppUtils instance
    @Provides
    @Singleton
    public AppUtils getAppUtils() {
        return new AppUtils(mContext);
    }

    //get location helper methods
    @Provides
    @Singleton
    public LocationHelper getLocationUtils() {
        return new LocationHelper(mContext);
    }

    //get location tracker
    @Provides
    @Singleton
    public LocationTracker getLocationTrackerOF() {
        return new LocationTracker(mContext);
    }


}