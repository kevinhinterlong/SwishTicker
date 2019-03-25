package com.hinterlong.kevin.swishticker;

import android.app.Application;

import com.hinterlong.kevin.swishticker.query.QueryEngine;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QueryEngine.init(this);
    }
}
