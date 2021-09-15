package com.example.finalproject.datas

class DataResponse(
    var token: String,
    var user: UserData,
    var appointments: List<AppointmentData>,
    var places: List<PlaceData>
) {
}