package com.neppplus.gabozago.datas

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.milliseconds

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
    enum class TimeValue(val value: Int,val maximum : Int, val msg : String) {
        SEC(60,60,"분 전"),
        MIN(60,24,"시간 전"),
        HOUR(24,30,"일 전"),
        DAY(30,12,"달 전"),
        MONTH(12,Int.MAX_VALUE,"년 전")
    }

    fun getFormattedDateTime(): String? {
        val curTime = System.currentTimeMillis()
        var gapTime = (curTime - this.createdAt.time) / 1000
        var msg: String? = null
        if (this.createdAt.time < TimeValue.SEC.value)
            msg = "방금 전"
        else {
            for (i in TimeValue.values()) {
                gapTime /= i.value
                if (gapTime < i.maximum) {
                    msg = "${gapTime}${i.msg}"
                    break
                }
            }
        }
        return msg
    }
}