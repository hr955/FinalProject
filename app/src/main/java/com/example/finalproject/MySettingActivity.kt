package com.example.finalproject

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivityMySettingBinding
import com.example.finalproject.utils.GlobalData

class MySettingActivity : BaseActivity() {

    lateinit var binding:ActivityMySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_setting)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        binding.layoutReadyTime.setOnClickListener {

        }
    }

    override fun setValues() {
        txtTitle.text = "프로필"
        binding.txtNickname.text = GlobalData.loginUser!!.nickname
    }
}