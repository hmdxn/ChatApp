package com.tronography.locationchat;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.tronography.locationchat.utils.UtilsModule;


public class ChatApplication extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = createComponent();
    }

    public AppComponent getAppComponent() {
        return component;
    }

    public AppComponent createComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .sharedPrefModule(new SharedPrefModule())
                .utilsModule(new UtilsModule(this))
                .build();
    }

}
