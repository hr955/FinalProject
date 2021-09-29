package com.neppplus.gabozago

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.neppplus.gabozago.databinding.ActivityChangePasswordBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.utils.ContextUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : BaseActivity() {

    lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        btnClose.setOnClickListener { finish() }

        binding.btnChangePassword.setOnClickListener {
            val currentPW = binding.edtCurrentPassword.text.toString()
            val newPW = binding.edtNewPassword.text.toString()
            val newPWCheck = binding.edtNewPasswordCheck.text.toString()

            if (newPW.length < 8) {
                binding.txtNewPasswordWarning.text = getString(R.string.password_length_warning)
                return@setOnClickListener
            }

            if (newPW != newPWCheck) {
                binding.txtNewPasswordWarning.text = getString(R.string.password_match_warning)
                return@setOnClickListener
            }

            if (currentPW == newPW) {
                binding.txtNewPasswordWarning.text = getString(R.string.curren_new_same_password)
                return@setOnClickListener
            }

            apiService.patchRequestChangePassword(currentPW, newPW)
                .enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if(response.isSuccessful){
                            Toast.makeText(mContext, "비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show()
                            ContextUtil.setToken(mContext, response.body()!!.data.token)
                            finish()
                        }else{
                            binding.txtPasswordWarning.text = getString(R.string.not_current_password)
                        }
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
                })
        }
    }

    override fun setValues() {
        txtTitle.text = "비밀번호 변경"
        btnClose.visibility = View.VISIBLE
    }
}