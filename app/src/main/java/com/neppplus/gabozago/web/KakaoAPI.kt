package com.neppplus.gabozago.web

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class KakaoAPI {
    companion object{

        fun getRetrofit(): KakaoAPIService {

            val retrofit =  Retrofit.Builder()
                .baseUrl("https://dapi.kakao.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(KakaoAPIService::class.java)
        }
    }
}