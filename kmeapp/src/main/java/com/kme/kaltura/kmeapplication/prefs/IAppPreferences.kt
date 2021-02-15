package com.kme.kaltura.kmeapplication.prefs

interface IAppPreferences {

    fun putString(key: String, value: String)

    fun getString(key: String, defaultValue: String = ""): String?

    fun putInt(key: String, value: Int)

    fun getInt(key: String, defaultValue: Int = 0): Int

    fun putLong(key: String, value: Long)

    fun getLong(key: String, defaultValue: Long = 0L): Long

    fun putBoolean(key: String, value: Boolean)

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

    fun clearCurrentPrefs(key: String)

    fun clear()

}
