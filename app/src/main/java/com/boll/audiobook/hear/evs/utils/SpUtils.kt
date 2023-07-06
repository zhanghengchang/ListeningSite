package com.boll.audiobook.hear.evs.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.boll.audiobook.hear.app.MyApplication

object SpUtils {

        var sharedPreferences: SharedPreferences? = null

        init {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.mInstance)
        }

        fun getString(key: String, defaultValue: String?): String? {
            return sharedPreferences?.getString(key, defaultValue)
        }

        fun putString(key: String, value: String) {
            sharedPreferences?.edit()?.putString(key, value)?.apply()
        }

        fun getInt(key: String, defaultValue: Int): Int? {
            return sharedPreferences?.getInt(key, defaultValue)
        }

        fun putInt(key: String, value: Int) {
            sharedPreferences?.edit()?.putInt(key, value)?.apply()
        }

        fun getBoolean(key: String, defaultValue: Boolean): Boolean? {
            return sharedPreferences?.getBoolean(key, defaultValue)
        }

        fun putBoolean(key: String, value: Boolean) {
            sharedPreferences?.edit()?.putBoolean(key, value)?.apply()
        }

        fun getFloat(key: String, defaultValue: Float): Float? {
            return sharedPreferences?.getFloat(key, defaultValue)
        }

        fun putFloat(key: String, value: Float) {
            sharedPreferences?.edit()?.putFloat(key, value)?.apply()
        }

        fun getLong(key: String, defaultValue: Long): Long? {
            return sharedPreferences?.getLong(key, defaultValue)
        }

        fun putLong(key: String, value: Long) {
            sharedPreferences?.edit()?.putLong(key, value)?.apply()
        }

}