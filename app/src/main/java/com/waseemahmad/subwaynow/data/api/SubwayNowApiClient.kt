package com.waseemahmad.subwaynow.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SubwayNowApiClient private constructor() {
    
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://subwaynow-api.vercel.app/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    val apiService: SubwayNowApiService = retrofit.create(SubwayNowApiService::class.java)
    
    companion object {
        @Volatile
        private var INSTANCE: SubwayNowApiClient? = null
        
        fun getInstance(): SubwayNowApiClient {
            return INSTANCE ?: synchronized(this) {
                val instance = SubwayNowApiClient()
                INSTANCE = instance
                instance
            }
        }
    }
}