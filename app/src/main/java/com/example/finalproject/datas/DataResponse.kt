package com.example.finalproject.datas

import com.google.gson.annotations.SerializedName

class DataResponse(
    var token: String,
    var user: UserData,
    var appointments: List<AppointmentData>,
    var places: List<PlaceData>,
    var friends: List<UserData>,
    var users: List<UserData>,
    var appointment: AppointmentData
) {
}