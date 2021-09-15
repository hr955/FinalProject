package com.example.finalproject.datas

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class AppointmentData(
    var id: Int,
    @SerializedName("user_id")
    var userId: Int,
    var title: String,
    var datetime: Date,
    var place: String,
    var latitude: Double,
    var longitude: Double,
    @SerializedName("created_at")
    var createdAt: Date,
    var user: UserData
) : Serializable {
}