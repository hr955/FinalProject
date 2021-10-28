package com.neppplus.gabozago.web

import com.neppplus.gabozago.datas.SearchPlaceData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoAPIService {
    // 키워드 검색
    @GET("v2/local/search/keyword.json")
    fun getRequestSearchPlace(
        @Header("Authorization") authorization: String,
        @Query("query") query: String
    ): Call<SearchPlaceData>

    // 주소 검색
    @GET("/v2/local/search/address.json")
    fun getRequestSearchAddress(
        @Header("Authorization") authorization: String,
        @Query("query") query: String
    ): Call<SearchPlaceData>
}