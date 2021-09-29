package com.neppplus.gabozago.datas

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class AppointmentData(
    var id: Int,
    @SerializedName("user_id")
    var userId: Int,
    var title: String,
    var datetime: Date,
    @SerializedName("start_place")
    var startPlace: String,
    @SerializedName("start_latitude")
    var startlatitude: Double,
    @SerializedName("start_longitude")
    var startlongitude: Double,
    var place: String,
    var latitude: Double,
    var longitude: Double,
    @SerializedName("created_at")
    var createdAt: Date,
    var user: UserData,
    @SerializedName("invited_friends")
    var invitedFriendList: List<UserData>
) : Serializable {

    fun getFormattedDateTime(): String {
        val nowDate = Calendar.getInstance()

        val diff = this.datetime.time - nowDate.timeInMillis
        val diffHour = diff / 1000 / 60 / 60

        if (diffHour in 1..0) {
            val diffMinute = diff / 1000 / 60
            return "약속 ${diffMinute}분 전"
        } else if (diffHour in 1..4) {
            return "약속 ${diffHour}시간 전"
        } else {
            val dateTimeFormat = SimpleDateFormat("M/d (E) a h:mm")
            return dateTimeFormat.format(this.datetime.time)
        }
    }
}