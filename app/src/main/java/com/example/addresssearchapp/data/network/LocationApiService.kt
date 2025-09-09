package com.example.addresssearchapp.data.network

import com.example.addresssearchapp.data.model.LocationFeature
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationApiService {
    @GET("search.php")
    suspend fun searchLocations(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 10
    ): Response<List<LocationFeature>>
}