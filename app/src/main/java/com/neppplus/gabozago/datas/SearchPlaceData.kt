package com.neppplus.gabozago.datas

import com.google.gson.annotations.SerializedName

data class SearchPlaceData(
    var documents: List<Documents>
)

data class Documents(
    @SerializedName("address_name")
    var addressName: String,
    @SerializedName("place_name")
    var placeName: String,
    @SerializedName("y")
    var latitude: Double,
    @SerializedName("x")
    var longitude: Double
)