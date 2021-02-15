package com.kme.kaltura.kmesdk.prefs

import android.content.Context
import android.content.SharedPreferences

/**
 * An implementation for the preferences storage
 */
internal class KmePreferencesImpl(context: Context) : IKmePreferences {

    private var sharedPref: SharedPreferences =
        context.getSharedPreferences("KMESDK_PREFERENCES", Context.MODE_PRIVATE)

    /**
     * Put [String] value to the preferences
     */
    override fun putString(key: String, value: String) {
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    /**
     * Getting [String] value from the preferences by key
     */
    override fun getString(key: String, defaultValue: String): String? {
        return sharedPref.getString(key, defaultValue)
    }

    /**
     * Put [Int] value to the preferences
     */
    override fun putInt(key: String, value: Int) {
        with(sharedPref.edit()) {
            putInt(key, value)
            apply()
        }
    }

    /**
     * Getting [Int] value from the preferences by key
     */
    override fun getInt(key: String, defaultValue: Int): Int {
        return sharedPref.getInt(key, defaultValue)
    }

    /**
     * Put [Long] value to the preferences
     */
    override fun putLong(key: String, value: Long) {
        with(sharedPref.edit()) {
            putLong(key, value)
            apply()
        }
    }

    /**
     * Getting [Long] value from the preferences by key
     */
    override fun getLong(key: String, defaultValue: Long): Long {
        return sharedPref.getLong(key, defaultValue)
    }

    /**
     * Put [Boolean] value to the preferences
     */
    override fun putBoolean(key: String, value: Boolean) {
        with(sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    /**
     * Getting [Boolean] value from the preferences by key
     */
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPref.getBoolean(key, defaultValue)
    }

    /**
     * Clears preference row by key value
     */
    override fun clearCurrentPrefs(key: String) {
        val editor = sharedPref.edit()
        if (sharedPref.contains(key)) {
            editor.remove(key)
        }
        editor.apply()
    }

    /**
     * Clears all stored preferences
     */
    override fun clear() {
        sharedPref.edit().clear().apply()
    }

}
