package com.example.finalproject

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivitySingUpBinding
import com.example.finalproject.datas.BasicResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO 비밀번호 일치 검사, 이메일 중복 및 양식 검사, 닉네임 중복검사

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
                    if(response.isSuccessful){
                        val basicResponse = response.body()!!

                    } else {
                        val jsonObj = JSONObject(response.errorBody()!!.string())
                        val message = jsonObj.getString("message")

                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                }
            })
        }
    }

    override fun setValues() {
    }
}