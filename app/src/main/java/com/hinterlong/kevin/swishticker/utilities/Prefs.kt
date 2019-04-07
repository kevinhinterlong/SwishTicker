package com.hinterlong.kevin.swishticker.utilities

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val NAME = "swishticker-prefs"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    // list of app specific preferences
    private val IS_FIRST_RUN_PREF = Pair("IS_FIRST_RUN_PREF", false)
    private val DEFAULT_TEAM_ID_PREF = Pair("DEFAULT_TEAM_ID", null)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var defaultTeamId: Long?
        get() = preferences.getString(DEFAULT_TEAM_ID_PREF.first, DEFAULT_TEAM_ID_PREF.second)?.toLong()
        set(value) = preferences.edit {
            it.putString(DEFAULT_TEAM_ID_PREF.first, value?.toString())
        }

    var isFirstRun: Boolean
        get() = preferences.getBoolean(IS_FIRST_RUN_PREF.first, IS_FIRST_RUN_PREF.second)
        set(value) = preferences.edit {
            it.putBoolean(IS_FIRST_RUN_PREF.first, value)
        }
}