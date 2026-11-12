package com.Sasfifu3.Wagraw.data.local

import android.content.Context
import android.content.SharedPreferences

class ChiliPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun readIdSet(key: String): Set<Int> =
        prefs.getStringSet(key, emptySet())
            .orEmpty()
            .mapNotNull { it.toIntOrNull() }
            .toSet()

    fun writeIdSet(key: String, value: Set<Int>) {
        prefs.edit().putStringSet(key, value.map { it.toString() }.toSet()).apply()
    }

    fun readNames(key: String): Set<String> = prefs.getStringSet(key, emptySet()).orEmpty()

    fun writeNames(key: String, value: Set<String>) {
        prefs.edit().putStringSet(key, value).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val STORE_NAME = "chili_heat_run_store"
    }
}
