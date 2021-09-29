package com.neppplus.gabozago.datas

import com.google.gson.annotations.SerializedName

class DataResponse(
    var token: String,
    var user: UserData,
    var appointments: List<AppointmentData>,
    var places: List<PlaceData>,
    var friends: List<UserData>,
    var users: List<UserData>,
    var appointment: AppointmentData,
    @SerializedName("unread_noty_count")
    var unreadNotyCount: Int,
    var notifications: List<NotificationData>,
    @SerializedName("invited_appointments")
    var invitedAppointments: List<AppointmentData>
) {
}