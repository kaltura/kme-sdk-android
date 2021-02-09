package com.kme.kaltura.kmesdk.prefs

/**
 * An interface for the preferences storage
 */
interface IKmePreferences {

    /**
     * Put [String] value to the preferences
     *
     * @param key preferences key
     * @param value preferences value
     */
    fun putString(key: String, value: String)

    /**
     * Getting [String] value from the preferences by key
     *
     * @param key preferences key
     * @param defaultValue default value in case key is not stored in the preferences
     * @return stored value if exist, otherwise [defaultValue]
     */
    fun getString(key: String, defaultValue: String = ""): String?

    /**
     * Put [Int] value to the preferences
     *
     * @param key preferences key
     * @param value preferences value
     */
    fun putInt(key: String, value: Int)

    /**
     * Getting [Int] value from the preferences by key
     *
     * @param key preferences key
     * @param defaultValue default value in case key is not stored in the preferences
     * @return stored value if exist, otherwise [defaultValue]
     */
    fun getInt(key: String, defaultValue: Int = 0): Int

    /**
     * Put [Long] value to the preferences
     *
     * @param key preferences key
     * @param value preferences value
     */
    fun putLong(key: String, value: Long)

    /**
     * Getting [Long] value from the preferences by key
     *
     * @param key preferences key
     * @param defaultValue default value in case key is not stored in the preferences
     * @return stored value if exist, otherwise [defaultValue]
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long

    /**
     * Put [Boolean] value to the preferences
     *
     * @param key preferences key
     * @param value preferences value
     */
    fun putBoolean(key: String, value: Boolean)

    /**
     * Getting [Boolean] value from the preferences by key
     *
     * @param key preferences key
     * @param defaultValue default value in case key is not stored in the preferences
     * @return stored value if exist, otherwise [defaultValue]
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

    /**
     * Clears preference row by key value
     *
     * @param key preferences key
     */
    fun clearCurrentPrefs(key: String)

    /**
     * Clears all stored preferences
     */
    fun clear()

}
