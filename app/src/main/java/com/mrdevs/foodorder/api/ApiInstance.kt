package com.mrdevs.foodorder.api

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiInstance {

    companion object {
        private const val BASE_URL = "http://192.168.0.50:8080"


        private val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        private var retrofit: Retrofit = retrofitBuilder.build()

        private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

        private val logging: HttpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC)

        fun <T> createService(
            context: Context,
            serviceClass: Class<T>,
            withToken: Boolean? = false
        ): T {
            httpClient.interceptors().clear()

            if (!httpClient.interceptors().contains(logging)) {
                httpClient.addInterceptor(logging)
            }
            if (withToken == true) {
                httpClient.addInterceptor(ApiAuthInterceptor(context))
            }

            retrofit = retrofitBuilder.client(httpClient.build()).build()
            return retrofit.create(serviceClass)
        }
    }
}
