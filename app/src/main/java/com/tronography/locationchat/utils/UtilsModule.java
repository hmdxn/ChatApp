package com.tronography.locationchat.utils;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UtilsModule {

    private Context context;

    public UtilsModule(Context context) {
        this.context = context;
    }

    // get AppUtils instance
    @Provides
    @Singleton
    public AppUtils getAppUtils() {
        return new AppUtils(context);
    }

    //get location helper methods
    @Provides
    @Singleton
    public LocationHelper getLocationUtils() {
        return new LocationHelper(context);
    }

    //get location tracker
    @Provides
    @Singleton
    public LocationTracker getLocationTracker() {
        return new LocationTracker(context);
    }

}