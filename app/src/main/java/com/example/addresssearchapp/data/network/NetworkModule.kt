package com.example.addresssearchapp.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    private const val LOCATION_BASE_URL = "https://us1.locationiq.com/v1/"
    private const val DIRECTIONS_BASE_URL = "https://maps.googleapis.com/maps/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val locationApiService: LocationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(LOCATION_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LocationApiService::class.java)
    }
    
    val directionsApiService: DirectionsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(DIRECTIONS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DirectionsApiService::class.java)
    }
}