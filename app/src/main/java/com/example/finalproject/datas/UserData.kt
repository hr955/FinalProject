package com.example.finalproject.datas

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserData(
    var id: Int,
    var provider: String,
    var email: String,
    @SerializedName("nick_name")
    var nickname: String,
    @SerializedName("ready_minute")
    var readyMinute: Int
) : Serializable {
}