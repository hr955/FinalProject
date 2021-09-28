package com.example.finalproject

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : BaseActivity() {

    lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
    }

    override fun setValues() {
        txtTitle.text = "비밀번호 변경"
        btnClose.visibility = View.VISIBLE
    }
}