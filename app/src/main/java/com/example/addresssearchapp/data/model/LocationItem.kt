package com.example.addresssearchapp.data.model

import com.google.gson.annotations.SerializedName

// The API returns an array of location items directly
data class LocationFeature(
	@SerializedName("place_id")
	val placeId: String,
	
	@SerializedName("display_name")
	val displayName: String,
	
	@SerializedName("lat")
	val lat: String,
	
	@SerializedName("lon")
	val lon: String
)

// Our app's internal model for locations
data class LocationItem(
	val id: String,
	val address: String,
	val latitude: Double,
	val longitude: Double
)
