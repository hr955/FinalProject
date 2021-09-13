package com.example.finalproject.datas

import com.google.gson.annotations.SerializedName

class UserResponse(
    var id: Int,
    @SerializedName("nick_name")
    var nickname: String,
    var email: String
) {
}