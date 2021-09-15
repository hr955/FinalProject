package com.example.finalproject.datas

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
    var place: String,
    var latitude: Double,
    var longitude: Double,
    @SerializedName("created_at")
    var createdAt: Date,
    var user: UserData
) : Serializable {

    fun getFormattedDateTime(): String {
        val nowDate = Calendar.getInstance()
        val diff = this.datetime.time - nowDate.timeInMillis
        val diffHour = diff / 1000 / 60 / 60

        if (diffHour < 1) {
            val diffMinute = diff / 1000 / 60
            return "약속 ${diffMinute}분 전"
        } else if (diffHour < 5) {
            return "약속 ${diffHour}시간 전"
        } else {
            val dateTimeFormat = SimpleDateFormat("M/d (E) a h:mm")
            return dateTimeFormat.format(this.datetime)
        }
    }
}