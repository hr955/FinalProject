package com.example.finalproject

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.finalproject.databinding.ActivityMySettingBinding
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.utils.ContextUtil
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

        binding.txtNickname.setOnClickListener {
            patchUserInfo("닉네임 입력", "nickname")
        }

        binding.layoutReadyTime.setOnClickListener {
            patchUserInfo("준비시간 입력", "ready_minute")
        }

        binding.layoutMyPlaceList.setOnClickListener {
            startActivity(Intent(mContext, ViewMyPlaceListActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            ContextUtil.setToken(mContext,"")
            val myIntent = Intent(mContext, LoginActivity::class.java)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(myIntent)
            finish()
        }
    }

    override fun setValues() {
        txtTitle.text = "프로필"
        Log.d("프로바이더", GlobalData.loginUser!!.provider)

        binding.ivLoginSocialLogo.apply {
            when(GlobalData.loginUser!!.provider){
                "facebook" -> {
                    setImageResource(R.drawable.ic_facebook_logo_color)
                    binding.layoutLogout.visibility = View.GONE
                }
                "kakao" -> {
                    setImageResource(R.drawable.ic_kakao_logo)
                    binding.layoutLogout.visibility = View.GONE
                }
                else -> visibility = View.GONE
            }
        }

        setUserInfo()
    }

    fun patchUserInfo(title: String, field: String){
        val et = EditText(mContext)
        et.setPadding(50,0,50,0)

        val alert = AlertDialog.Builder(mContext)
        alert.setTitle(title)
        alert.setView(et)
        alert.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->
            apiService.patchRequestMyInfo(field, et.text.toString())
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
        alert.setNegativeButton("취소",null)
        alert.show()
    }

    fun setUserInfo() {
        val loginUser = GlobalData.loginUser!!

        binding.txtNickname.text = loginUser!!.nickname

        Glide.with(mContext).load(loginUser.profileImgURL).into(binding.ivProfile)

        val userReadyMinute = loginUser.readyMinute

        if (loginUser.readyMinute >= 60) {
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