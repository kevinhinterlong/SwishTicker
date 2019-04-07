package com.hinterlong.kevin.swishticker

import android.app.Application
import com.hinterlong.kevin.swishticker.utilities.Prefs
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        Prefs.init(this)
        Timber.plant(Timber.DebugTree())
    }
}
