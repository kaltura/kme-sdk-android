package com.kme.kaltura.kmesdk.prefs

import android.content.Context

class KmePreferences(context: Context) : Prefs(context) {

    fun getString(key: String): String? {
        return super.getString(key, "")
    }

    fun getInt(key: String): Int {
        return super.getInt(key, 0)
    }

    fun getLong(key: String): Long {
        return super.getLong(key, 0)
    }

    fun getBoolean(key: String): Boolean {
        return super.getBoolean(key, false)
    }

}
