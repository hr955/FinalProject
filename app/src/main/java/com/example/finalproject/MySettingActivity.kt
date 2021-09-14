package com.example.finalproject

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivityMySettingBinding
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.utils.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MySettingActivity : BaseActivity() {

    lateinit var binding: ActivityMySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_setting)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        val et = EditText(mContext)

        binding.layoutReadyTime.setOnClickListener {
            val alert = AlertDialog.Builder(mContext)
            alert.setTitle("준비시간 입력")
            alert.setView(et)
            alert.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->
                apiService.patchRequestMyInfo("ready_minute", et.text.toString())
                    .enqueue(object : Callback<BasicResponse> {
                        override fun onResponse(
                            call: Call<BasicResponse>,
                            response: Response<BasicResponse>
                        ) {
                            GlobalData.loginUser = response.body()!!.data.user
                            setUserInfo()
                        }

                        override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
                    })
            })
            alert.show()
        }
    }

    override fun setValues() {
        txtTitle.text = "프로필"

        setUserInfo()
    }

    fun setUserInfo() {
        binding.txtNickname.text = GlobalData.loginUser!!.nickname

        val userReadyMinute = GlobalData.loginUser!!.readyMinute

        if (GlobalData.loginUser!!.readyMinute >= 60) {
            val hour = userReadyMinute / 60
            val minute = userReadyMinute % 60
            if (minute == 0) {
                binding.txtReadyTime.text = "${hour}시간"
            } else {
                binding.txtReadyTime.text = "${hour}시간 ${minute}분"
            }
        } else {
            binding.txtReadyTime.text = "${userReadyMinute}분"
        }
    }
}