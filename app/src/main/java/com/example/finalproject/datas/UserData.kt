package com.example.finalproject.datas

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class UserData(
    var id: Int,
    var provider: String,
    var email: String,
    @SerializedName("nick_name")
    var nickname: String,
    @SerializedName("profile_img")
    var profileImgURL: String,
    @SerializedName("ready_minute")
    var readyMinute: Int,
    @SerializedName("arrived_at")
    var arrivedAt: Date?
) : Serializable {
}