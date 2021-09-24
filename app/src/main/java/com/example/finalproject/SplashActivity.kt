package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.utils.ContextUtil
import com.example.finalproject.utils.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
    }

    override fun setValues() {
        GlobalData.context = mContext

        val myHandler = Handler(Looper.getMainLooper())

        apiService.getRequestUserData().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(
                call: Call<BasicResponse>,
                response: Response<BasicResponse>
            ) {
                if (response.isSuccessful) {
                    GlobalData.loginUser = response.body()!!.data.user
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
        })

        myHandler.postDelayed({
            val myIntent: Intent

            if (ContextUtil.getToken(mContext) != "" && GlobalData.loginUser != null) {
                myIntent = Intent(mContext, MainActivity::class.java)
            } else {
                myIntent = Intent(mContext, LoginActivity::class.java)
            }

            startActivity(myIntent)
            finish()
        }, 1000)
    }
}