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
        return SimpleDateFormat("M/d (E) a h:mm").format(this.datetime.time - Calendar.getInstance().timeZone.rawOffset)
    }

    fun getDDay(): String? {
        val ONE_DAY = 24 * 60 * 60 * 1000
        val ONE_HOUR = 60 * 60 * 1000
        val ONE_MINUTE = 60 * 1000
        val nowDate = Calendar.getInstance()
        val nowDateMillis = nowDate.timeInMillis
        val dateTimeToTimeZone = this.datetime.time - nowDate.timeZone.rawOffset

        val diffMin = dateTimeToTimeZone / ONE_MINUTE - nowDateMillis / ONE_MINUTE
        val diffHour = dateTimeToTimeZone / ONE_HOUR - nowDateMillis / ONE_HOUR
        val diffDay = dateTimeToTimeZone / ONE_DAY - nowDateMillis / ONE_DAY

        return if (diffMin <= 0){
            null
        }else if (diffMin <= 59){
            "${diffMin}분 전"
        }else if (diffHour <= 23){
            "${diffHour}시간 전"
        }else {
            "D - $diffDay"
        }
    }
}