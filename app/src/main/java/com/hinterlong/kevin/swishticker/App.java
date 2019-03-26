package com.hinterlong.kevin.swishticker;

import android.app.Application;

import timber.log.Timber;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QueryEngine.init(this);

        Timber.plant(new Timber.DebugTree());
    }
}
