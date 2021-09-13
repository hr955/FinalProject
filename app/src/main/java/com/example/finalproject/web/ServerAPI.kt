package com.example.finalproject.web

import android.content.Context
import com.example.finalproject.utils.ContextUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServerAPI {
    companion object {
        private val HOST_URL = "http://3.36.146.152"

        private var retrofit: Retrofit? = null

        fun getRetrofit(context: Context): Retrofit {

            val interceptor = Interceptor {
                with(it) {
                    val newRequest =
                        request().newBuilder()
                            .addHeader("X-Http-Token", ContextUtil.getToken(context))
                            .build()

                    proceed(newRequest)
                }
            }

            val myClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(HOST_URL)
                    .client(myClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return retrofit!!
        }
    }
}