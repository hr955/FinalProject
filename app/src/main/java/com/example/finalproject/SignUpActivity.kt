package com.example.finalproject

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivitySingUpBinding
import com.example.finalproject.datas.BasicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : BaseActivity() {

    lateinit var binding: ActivitySingUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sing_up)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        binding.btnSignUp.setOnClickListener {
            apiService.putRequestSingUp(
                binding.edtEmail.text.toString(),
                binding.edtPassword.text.toString(),
                binding.edtNickname.text.toString()
            ).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    val basicResponse = response.body()!!
                    Log.d("서버메세지", basicResponse.message)
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                }
            })
        }
    }

    override fun setValues() {
    }
}