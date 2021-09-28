package com.example.finalproject.datas

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

class NotificationData(
    var id: Int,
    var message : String,
    var title: String,
    var type: String,
    @SerializedName("act_user")
    var actUser: UserData,
    @SerializedName("created_at")
    var createdAt: Date,
    @SerializedName("is_read")
    var isRead: Boolean
) {

    fun getFormattedDateTime(): String {
        val nowDate = Calendar.getInstance()

        // 디바이스 시간과 맞도록 시간 보정
        val dateTimeToTimeZone = this.createdAt.time + nowDate.timeZone.rawOffset

        // 몇시간 전에 온 알림인지 시간 계산
        val diff = nowDate.timeInMillis - dateTimeToTimeZone
        val diffHour = diff / 1000 / 60 / 60
        val diffDay = diffHour/24

        if (diffHour < 1) {
            val diffMinute = diff / 1000 / 60
            return "${diffMinute}분 전"
        } else if (diffHour < 24) {
            return "${diffHour}시간 전"
        } else if (diffHour >= 24 && diffDay <7) {
            return "${diffDay}일 전"
        } else {
            val dateTimeFormat = SimpleDateFormat("yyyy.MM.dd")
            return dateTimeFormat.format(dateTimeToTimeZone)
        }
    }
}