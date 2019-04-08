package com.hinterlong.kevin.swishticker.utilities

import android.view.Window
import android.view.WindowManager

fun setFullScreen(window: Window?) = window?.let {
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(window.attributes)
    lp.width = WindowManager.LayoutParams.MATCH_PARENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    window.attributes = lp
}