package com.example.finalproject.datas

import com.google.gson.annotations.SerializedName

class UserData(
    var id: Int,
    provider: String,
    @SerializedName("nick_name")
    var nickname: String,
    var email: String
) {
}