package com.neppplus.gabozago

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.neppplus.gabozago.databinding.ActivitySingUpBinding
import com.neppplus.gabozago.datas.BasicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : BaseActivity() {

    lateinit var binding: ActivitySingUpBinding
    private var mPWLengthFlag = false
    private var mPWMatchFlag = false
    private var mEmailFlag = false
    private var mNicknameFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sing_up)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        singUpButtonClickEvent()
        checkEmailDuplButtonClickEvent()
        nicknameDuplCheckButtonClickEvent()
    }

    override fun setValues() {
        checkPasswordLength()
        checkPasswordMatch()
        emailTextWatcher()
        nicknameTextWatcher()
        hideKeyboard()
    }

    // 이메일 중복검사 버튼이벤트
    private fun checkEmailDuplButtonClickEvent() {
        binding.btnCheckEmail.setOnClickListener {
            val email = binding.edtEmail.text.toString()

            // 이메일 양식이 아닐 경우
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                setWarning(binding.txtEmailWarning, getString(R.string.email_pattern_check))
                return@setOnClickListener
            }

            // 이메일 중복검사 API 호출
            apiService.getRequestDuplCheck("EMAIL", email)
                .enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body()!!.code == 200) {
                                setUsable(binding.txtEmailWarning, getString(R.string.email_usable))
                                mEmailFlag = true
                            }
                        } else {
                            setWarning(binding.txtEmailWarning, getString(R.string.email_unusable))
                            mEmailFlag = false
                        }
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
                })
        }
    }

    // 텍스트 수정시 emailFlag를 false로 설정 (중복검사 필요)
    private fun emailTextWatcher() {
        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mEmailFlag = false
                binding.txtEmailWarning.visibility = View.INVISIBLE
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    // 비밀번호 길이 검사
    private fun checkPasswordLength() {
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length >= 8) {
                    binding.txtPasswordWarning.visibility = View.INVISIBLE
                    mPWLengthFlag = true
                    if (binding.edtPasswordCheck.text.toString() == p0.toString()) {
                        setUsable(binding.txtPasswordWarning, getString(R.string.password_usable))
                        mPWMatchFlag = true
                    }
                } else {
                    setWarning(
                        binding.txtPasswordWarning,
                        getString(R.string.password_length_warning)
                    )
                    mPWLengthFlag = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    // 비밀번호 확인 일치여부 검사
    private fun checkPasswordMatch() {
        binding.edtPasswordCheck.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (mPWLengthFlag) {
                    mPWMatchFlag = if (binding.edtPassword.text.toString() == p0.toString()) {
                        setUsable(binding.txtPasswordWarning, getString(R.string.password_usable))
                        true
                    } else {
                        setWarning(
                            binding.txtPasswordWarning,
                            getString(R.string.password_match_warning)
                        )
                        false
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun nicknameDuplCheckButtonClickEvent() {
        binding.btnCheckNickname.setOnClickListener {
            val nickname = binding.edtNickname.text.toString()

            // 닉네임의 길이가 2자 미만인 경우
            if (nickname.length < 2) {
                setWarning(binding.txtNicknameWarning, getString(R.string.nickname_length_warning))
                return@setOnClickListener
            }

            // 닉네임 중복검사 API 호출
            apiService.getRequestDuplCheck("NICK_NAME", nickname)
                .enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body()!!.code == 200) {
                                setUsable(
                                    binding.txtNicknameWarning,
                                    getString(R.string.nickname_usable)
                                )
                                mNicknameFlag = true
                            }
                        } else {
                            setWarning(
                                binding.txtNicknameWarning,
                                getString(R.string.nickname_unusable)
                            )
                            mNicknameFlag = false
                        }
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
                })
        }
    }

    private fun nicknameTextWatcher() {
        binding.edtNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mNicknameFlag = false
                binding.txtNicknameWarning.visibility = View.INVISIBLE
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    // 회원가입 버튼 클릭이벤트
    private fun singUpButtonClickEvent() {
        binding.btnSignUp.setOnClickListener {
            if (binding.edtEmail.text.isEmpty()) {
                binding.edtEmail.requestFocus()
                setWarning(binding.txtEmailWarning, getString(R.string.email_edt_empty))
                return@setOnClickListener
            }

            if (!mEmailFlag) {
                binding.edtEmail.requestFocus()
                setWarning(binding.txtEmailWarning, getString(R.string.email_check_warning))
                return@setOnClickListener
            }

            if (!mPWLengthFlag) {
                binding.edtPassword.requestFocus()
                setWarning(binding.txtPasswordWarning, getString(R.string.password_length_warning))
                return@setOnClickListener
            }

            if (!mPWMatchFlag) {
                binding.edtPasswordCheck.requestFocus()
                setWarning(binding.txtPasswordWarning, getString(R.string.password_match_warning))
                return@setOnClickListener
            }

            if (!mNicknameFlag) {
                binding.edtNickname.requestFocus()
                setWarning(binding.txtNicknameWarning, getString(R.string.nickname_check_warning))
                return@setOnClickListener
            }

            callSingUpAPI()
        }
    }

    // 회원가입 API 호출
    private fun callSingUpAPI() {
        apiService.putRequestSingUp(
            binding.edtEmail.text.toString(),
            binding.edtPassword.text.toString(),
            binding.edtNickname.text.toString()
        ).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(
                call: Call<BasicResponse>,
                response: Response<BasicResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(mContext, "회원가입이 완료되었습니다\n로그인을 진행해주세요", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
        })
    }

    fun hideKeyboard(){
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if(view == null){
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setWarning(resId: TextView, warningMessage: String) {
        resId.apply {
            visibility = View.VISIBLE
            setTextColor(Color.RED)
            text = warningMessage
        }
    }

    fun setUsable(resId: TextView, usableMessage: String) {
        resId.apply {
            visibility = View.VISIBLE
            setTextColor(ContextCompat.getColor(mContext, R.color.green))
            text = usableMessage
        }
    }
}