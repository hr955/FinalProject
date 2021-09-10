package com.example.finalproject.web

import com.example.finalproject.datas.BasicResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.PUT

interface ServerAPIService {
    @FormUrlEncoded
    @PUT("/user")
    fun putRequestSingUp(
        @Field("email") email: String,
        @Field("password") pw: String,
        @Field("nick_name") nickname: String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("/user")
    fun postRequestLogin(
        @Field("email") email: String,
        @Field("password") pw: String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("/user/social")
    fun postRequestSocialLogin(
        @Field("provider") provider: String,
        @Field("uid") uid: String,
        @Field("nick_name") nickname: String
    ): Call<BasicResponse>
}