package com.example.finalproject

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.web.ServerAPI
import com.example.finalproject.web.ServerAPIService
import retrofit2.Retrofit
import retrofit2.create

abstract class BaseActivity : AppCompatActivity() {

    lateinit var mContext: Context

    private lateinit var retrofit: Retrofit
    lateinit var apiService : ServerAPIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = this

        retrofit = ServerAPI.getRetrofit(mContext)
        apiService = retrofit.create(ServerAPIService::class.java)
    }

    abstract fun setupEvents()
    abstract fun setValues()

}