package com.neppplus.gabozago.web

import com.neppplus.gabozago.datas.SearchPlaceData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoAPIService {
    @GET("v2/local/search/keyword.json")
    fun getRequestSearchPlace(
        @Header("Authorization") authorization: String,
        @Query("query") query: String
    ): Call<SearchPlaceData>
}