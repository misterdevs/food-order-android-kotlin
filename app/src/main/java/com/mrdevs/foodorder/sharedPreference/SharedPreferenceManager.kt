package com.mrdevs.foodorder.sharedPreference

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceManager(context: Context) {

    enum class KEYS { SP_USERNAME, SP_FULL_NAME, SP_TOKEN, SP_IS_LOGIN }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("LoggedIn", Context.MODE_PRIVATE)
    private val spEditor = sharedPreferences.edit()


    fun saveSpString(spKey: String, value: String) {
        spEditor.putString(spKey, value)
        spEditor.apply()
    }

    fun saveSpInt(spKey: String, value: Int) {
        spEditor.putInt(spKey, value)
        spEditor.apply()
    }

    fun saveSpBoolean(spKey: String, value: Boolean) {
        spEditor.putBoolean(spKey, value)
        spEditor.apply()
    }

    fun getFullName(): String? {
        return sharedPreferences.getString(KEYS.SP_FULL_NAME.name, "")
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(KEYS.SP_USERNAME.name, "")
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEYS.SP_TOKEN.name, "")
    }

    fun isLogin(): Boolean {
        return sharedPreferences.getBoolean(KEYS.SP_IS_LOGIN.name, false)
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key.toString(), "")
    }

    fun clearSession() {
        spEditor.clear()
        spEditor.apply()
    }


}