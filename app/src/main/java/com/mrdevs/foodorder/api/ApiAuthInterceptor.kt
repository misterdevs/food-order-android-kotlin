package com.mrdevs.foodorder.api

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.mrdevs.foodorder.LoginActivity
import com.mrdevs.foodorder.sharedPreference.SharedPreferenceManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ApiAuthInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPreferences = SharedPreferenceManager(context)
        val request = chain.request()
        val newRequest: Request = request.newBuilder()
            .header("Authorization", "Bearer ${sharedPreferences.getToken()}")
            .build()
        val response = chain.proceed(newRequest)

        if (response.code == 401) {
            logout()
        }

        return response
    }

    fun logout() {
        val sharedPreferences = SharedPreferenceManager(context)
        sharedPreferences.clearSession()

        val intent = Intent(context, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }
}