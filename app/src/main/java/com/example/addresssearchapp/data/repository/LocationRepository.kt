package com.example.addresssearchapp.data.repository

import com.example.addresssearchapp.data.model.LocationItem
import com.example.addresssearchapp.data.network.LocationApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationRepository(private val apiService: LocationApiService) {

    // API key is stored here for demo purposes only
    // In a real app, this should be stored securely (e.g., in environment variables or a secure storage)
    private val apiKey = "YOUR_LOCATIONIQ_API_KEY"

    suspend fun searchLocations(query: String): Result<List<LocationItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchLocations(apiKey, query)
                if (response.isSuccessful) {
                    val locations = response.body()?.map { feature ->
                        LocationItem(
                            id = feature.placeId,
                            address = feature.displayName,
                            latitude = feature.lat.toDouble(),
                            longitude = feature.lon.toDouble()
                        )
                    } ?: emptyList()
                    Result.success(locations)
                } else {
                    Result.failure(Exception("API Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}