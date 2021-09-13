package com.example.finalproject.web

import com.example.finalproject.datas.BasicResponse
import retrofit2.Call
import retrofit2.http.*

interface ServerAPIService {
    // 회원가입 API
    @FormUrlEncoded
    @PUT("/user")
    fun putRequestSingUp(
        @Field("email") email: String,
        @Field("password") pw: String,
        @Field("nick_name") nickname: String
    ): Call<BasicResponse>

    // 로그인 API
    @FormUrlEncoded
    @POST("/user")
    fun postRequestLogin(
        @Field("email") email: String,
        @Field("password") pw: String
    ): Call<BasicResponse>

    // 소셜로그인 API
    @FormUrlEncoded
    @POST("/user/social")
    fun postRequestSocialLogin(
        @Field("provider") provider: String,
        @Field("uid") uid: String,
        @Field("nick_name") nickname: String
    ): Call<BasicResponse>

    // 일정 목록 추가 API
    @FormUrlEncoded
    @POST("/appointment")
    fun postRequestAddAppointment(
        @Field("title") title: String,
        @Field("datetime") date: String,
        @Field("place") place: String,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double
        ): Call<BasicResponse>

    // 일정 목록 조회 API
    @GET("/appointment")
    fun getRequestAppointmentList(): Call<BasicResponse>

    // 사용자 정보 조회 API
    @GET("/user")
    fun getRequestUserData(): Call<BasicResponse>
}