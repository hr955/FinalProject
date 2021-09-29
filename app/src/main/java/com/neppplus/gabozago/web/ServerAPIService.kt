package com.neppplus.gabozago.web

import com.neppplus.gabozago.datas.BasicResponse
import okhttp3.MultipartBody
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

    // 이메일 및 닉네임 중복검사
    @GET("/user/check")
    fun getRequestDuplCheck(
        @Query("type") type: String,
        @Query("value") value: String
    ): Call<BasicResponse>

    // 일정 목록 추가 API
    @FormUrlEncoded
    @POST("/appointment")
    fun postRequestAddAppointment(
        @Field("title") title: String,
        @Field("datetime") date: String,
        @Field("start_place") startPlace: String,
        @Field("start_latitude") startLatitude: Double,
        @Field("start_longitude") startLongitude: Double,
        @Field("place") place: String,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double,
        @Field("friend_list") friendList: String
    ): Call<BasicResponse>

    // 일정 목록 조회 API
    @GET("/appointment")
    fun getRequestAppointmentList(): Call<BasicResponse>

    // 사용자 정보 조회 API
    @GET("/user")
    fun getRequestUserData(): Call<BasicResponse>

    // 회원 정보 수정
    @FormUrlEncoded
    @PATCH("/user")
    fun patchRequestMyInfo(
        @Field("field") field: String,
        @Field("value") value: String
    ): Call<BasicResponse>

    // 출발지 추가
    @FormUrlEncoded
    @POST("/user/place")
    fun postRequestAddMyPlace(
        @Field("name") title: String,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double,
        @Field("is_primary") isPrimary: Boolean
    ): Call<BasicResponse>

    // 출발지 목록 조회
    @GET("/user/place")
    fun getRequestMyPlaceList(): Call<BasicResponse>

    // 프로필 이미지 변경
    @Multipart
    @PUT("/user/image")
    fun putRequestProfileImage(
        @Part profileImg: MultipartBody.Part
    ): Call<BasicResponse>

    // 출발지 목록 조회
    @GET("/user/friend")
    fun getRequestFriendList(
        @Query("type") type: String
    ): Call<BasicResponse>

    // 사용자 목록 조회
    @GET("/search/user")
    fun getRequestSearchUser(
        @Query("nickname") nickname: String
    ): Call<BasicResponse>

    // 친구 요청하기
    @FormUrlEncoded
    @POST("/user/friend")
    fun postRequestFriend(
        @Field("user_id") userId: Int,
    ): Call<BasicResponse>

    // 친구 요청 수락/거절 API
    @FormUrlEncoded
    @PUT("/user/friend")
    fun putRequestFriendRequestResponse(
        @Field("user_id") userId: Int,
        @Field("type") type: String
    ): Call<BasicResponse>

    // 도착 인증 API
    @FormUrlEncoded
    @POST("/appointment/arrival")
    fun postRequestArrival(
        @Field("appointment_id") appointmentId: Int,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double,
    ): Call<BasicResponse>


    // 약속 상세 조회 API
    @GET("/appointment/{appointment_id}")
    fun getRequestAppointmentDetail(
        @Path("appointment_id") id: Int
    ): Call<BasicResponse>

    // 알림 목록 조회 API
    @GET("/notifications")
    fun getRequestNotificationList(
        @Query("need_all_notis") needAllNotis: String?
    ): Call<BasicResponse>

    // 알림 읽음처리 API
    @FormUrlEncoded
    @POST("/notifications")
    fun postRequestNotiIsRead(
        @Field("noti_id") appointmentId: Int
    ): Call<BasicResponse>

    // 약속 삭제 API
    @DELETE("/appointment")
    fun deleteRequestAppointment(
        @Query("appointment_id") appointmentId: Int
    ): Call<BasicResponse>

    // 약속 삭제 API
    @DELETE("/user/place")
    fun deleteRequestMyPlace(
        @Query("place_id") placeId: Int
    ): Call<BasicResponse>

    // 비밀번호 변경 API
    // 회원 정보 수정
    @FormUrlEncoded
    @PATCH("/user/password")
    fun patchRequestChangePassword(
        @Field("current_password") currentPassword: String,
        @Field("new_password") newPassword: String
    ): Call<BasicResponse>

}