package com.example.finalproject.web

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServerAPI {
    companion object {
        private val HOST_URL = "http://3.36.146.152"

        private var retrofit: Retrofit? = null

        fun getRetrofit() : Retrofit {
            if (retrofit == null){
                retrofit = Retrofit.Builder()
                    .baseUrl(HOST_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return retrofit!!
        }
    }
}