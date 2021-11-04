package com.neppplus.gabozago

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.neppplus.gabozago.databinding.ActivityEditNicknameBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.utils.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditNicknameActivity : BaseActivity() {

    lateinit var binding: ActivityEditNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_nickname)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        binding.btnSave.setOnClickListener {
            if (binding.edtNickname.text.toString().length < 2) {
                binding.txtNicknameWarning.text = getString(R.string.nickname_length_warning)
                return@setOnClickListener
            }
            if (binding.edtNickname.text.toString() == GlobalData.loginUser!!.nickname) {
                finish()
                return@setOnClickListener
            }
            saveNickname()
        }
    }

    override fun setValues() {
    }

    private fun saveNickname() {
        apiService.patchRequestMyInfo("nickname", binding.edtNickname.text.toString())
            .enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.code() == 400) {
                        binding.txtNicknameWarning.text = getString(R.string.nickname_unusable)
                    } else {
                        GlobalData.loginUser = response.body()!!.data.user
                        setResult(RESULT_OK)
                        finish()
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
            })

    }
}