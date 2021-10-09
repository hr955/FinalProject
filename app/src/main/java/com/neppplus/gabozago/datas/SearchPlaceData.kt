package com.neppplus.gabozago.datas

import com.google.gson.annotations.SerializedName

class SearchPlaceData(
    var addressName: String,
    var placeName: String,
    var latitude: Double,
    var longtitude: Double
) {
}