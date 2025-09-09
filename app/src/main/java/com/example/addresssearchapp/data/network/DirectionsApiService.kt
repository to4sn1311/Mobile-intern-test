package com.example.addresssearchapp.data.network

import com.example.addresssearchapp.data.model.DirectionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApiService {
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String = "driving",
        @Query("key") apiKey: String
    ): Response<DirectionsResponse>
}
