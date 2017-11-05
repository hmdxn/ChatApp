package com.tronography.locationchat;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Application app;

    public AppModule(Application app) {
        this.app = app;
    }

    @Provides
    public Application provideApplication() {
        return app;
    }

    @Provides
    public Context provideApplicationContext() {
        return app.getApplicationContext();
    }
}
