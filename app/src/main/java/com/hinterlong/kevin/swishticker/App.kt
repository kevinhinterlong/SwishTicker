package com.hinterlong.kevin.swishticker

import android.app.Application

import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        QueryEngine.init(this)

        Timber.plant(Timber.DebugTree())
    }
}
