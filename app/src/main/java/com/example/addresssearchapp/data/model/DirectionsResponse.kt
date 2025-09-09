package com.example.addresssearchapp.data.model

import com.google.gson.annotations.SerializedName

data class DirectionsResponse(
    @SerializedName("routes")
    val routes: List<Route>,
    @SerializedName("status")
    val status: String
)

data class Route(
    @SerializedName("legs")
    val legs: List<Leg>,
    @SerializedName("overview_polyline")
    val overviewPolyline: OverviewPolyline
)

data class Leg(
    @SerializedName("distance")
    val distance: Distance,
    @SerializedName("duration")
    val duration: Duration,
    @SerializedName("steps")
    val steps: List<Step>
)

data class Distance(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int
)

data class Duration(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int
)

data class Step(
    @SerializedName("distance")
    val distance: Distance,
    @SerializedName("duration")
    val duration: Duration,
    @SerializedName("html_instructions")
    val htmlInstructions: String,
    @SerializedName("travel_mode")
    val travelMode: String
)

data class OverviewPolyline(
    @SerializedName("points")
    val points: String
)
