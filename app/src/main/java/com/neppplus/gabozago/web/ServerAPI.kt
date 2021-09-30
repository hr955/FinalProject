package com.neppplus.gabozago.web

import android.content.Context
import com.neppplus.gabozago.utils.ContextUtil
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

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

            val gson =
                GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm")
                    .registerTypeAdapter(Date::class.java, DateDeserializer()).create()

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(HOST_URL)
                    .client(myClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }

            return retrofit!!
        }
    }
}