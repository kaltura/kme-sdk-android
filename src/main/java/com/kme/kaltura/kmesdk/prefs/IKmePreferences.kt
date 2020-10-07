package com.kme.kaltura.kmesdk.prefs

interface IKmePreferences {

    fun putString(key: String, value: String)

    fun getString(key: String, defaultValue: String): String?

    fun putInt(key: String, value: Int)

    fun getInt(key: String, defaultValue: Int): Int

    fun putLong(key: String, value: Long)

    fun getLong(key: String, defaultValue: Long): Long

    fun putBoolean(key: String, value: Boolean)

    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    fun clearCurrentPrefs(key: String)

    fun clear()

}
