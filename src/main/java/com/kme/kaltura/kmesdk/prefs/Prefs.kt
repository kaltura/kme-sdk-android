package com.kme.kaltura.kmesdk.prefs

import android.content.Context
import android.content.SharedPreferences

abstract class Prefs(
    context: Context
) : IKmePreferences {

    private var sharedPref: SharedPreferences =
        context.getSharedPreferences("KMESDK_PREFERENCES", Context.MODE_PRIVATE)

    override fun putString(key: String, value: String) {
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    override fun getString(key: String, defaultValue: String): String? {
        return sharedPref.getString(key, defaultValue)
    }

    override fun putInt(key: String, value: Int) {
        with(sharedPref.edit()) {
            putInt(key, value)
            apply()
        }
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return sharedPref.getInt(key, defaultValue)
    }

    override fun putLong(key: String, value: Long) {
        with(sharedPref.edit()) {
            putLong(key, value)
            apply()
        }
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return sharedPref.getLong(key, defaultValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        with(sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPref.getBoolean(key, defaultValue)
    }

    override fun clearCurrentPrefs(key: String) {
        val editor = sharedPref.edit()
        if (sharedPref.contains(key)) {
            editor.remove(key)
        }
        editor.apply()
    }

    override fun clear() {
        sharedPref.edit().clear().apply()
    }

}
