package com.example.finalproject

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivityLoginBinding
import com.facebook.login.LoginResult
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.finalproject.datas.BasicResponse
import com.facebook.*
import com.facebook.login.LoginManager
import com.kakao.sdk.user.UserApiClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginActivity : BaseActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        setupEvents()
        setValues()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun setupEvents() {
        //로그인 버튼 클릭
        binding.btnLogin.setOnClickListener {
            apiService.postRequestLogin(
                binding.edtEmail.text.toString(),
                binding.edtPassword.text.toString()
            ).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(mContext, response.body()!!.message, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val jsonObj = JSONObject(response.errorBody()!!.string())
                        Toast.makeText(mContext, jsonObj.getString("message"), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                }
            })
        }

        // 회원가입 버튼 클릭
        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(mContext, SignUpActivity::class.java))
        }

        // 카카오 로그인 버튼 클릭
        binding.btnKakaoLogin.setOnClickListener {
            UserApiClient.instance.loginWithKakaoAccount(mContext) { token, error ->
                if (error != null) {
                    Log.e("카카오로그인", "로그인 실패", error)
                } else if (token != null) {
                    Log.i("카카오로그인", "로그인 성공 ${token.accessToken}")

                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e("카카오로그인", "사용자 정보 요청 실패", error)
                        } else if (user != null) {
                            Log.i(
                                "카카오로그인", "사용자 정보 요청 성공" +
                                        "\n회원번호: ${user.id}" +
                                        "\n이메일: ${user.kakaoAccount?.email}" +
                                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                            )
                            apiService.postRequestSocialLogin(
                                "kakao",
                                user.id.toString(),
                                user.kakaoAccount?.profile?.nickname.toString()
                            ).enqueue(object : Callback<BasicResponse> {
                                override fun onResponse(
                                    call: Call<BasicResponse>,
                                    response: Response<BasicResponse>
                                ) {
                                    Toast.makeText(
                                        mContext,
                                        response.body()!!.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onFailure(
                                    call: Call<BasicResponse>,
                                    t: Throwable
                                ) {
                                }
                            })

                        }
                    }
                }
            }
        }

        callbackManager = CallbackManager.Factory.create()

        // 페이스북 로그인 버튼 클릭
        binding.btnFacebookLogin.setOnClickListener {
            val loginManager = LoginManager.getInstance()

            loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile"))

            loginManager.registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(result: LoginResult?) {
                        // 로그인 성공시 -> 페이스북에 접근할 수 있는 토큰 발급
                        // 유저 정보 받아오기
                        val graphRequest = GraphRequest.newMeRequest(result?.accessToken,
                            object : GraphRequest.GraphJSONObjectCallback {
                                override fun onCompleted(
                                    obj: JSONObject?,
                                    response: GraphResponse?
                                ) {
                                    val name = obj!!.getString("name")
                                    val id = obj!!.getString("id")

                                    apiService.postRequestSocialLogin("facebook", id, name)
                                        .enqueue(object : Callback<BasicResponse> {
                                            override fun onResponse(
                                                call: Call<BasicResponse>,
                                                response: Response<BasicResponse>
                                            ) {
                                                Toast.makeText(
                                                    mContext,
                                                    response.body()!!.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                            override fun onFailure(
                                                call: Call<BasicResponse>,
                                                t: Throwable
                                            ) {
                                            }
                                        })
                                }
                            })
                        graphRequest.executeAsync()
                    }

                    override fun onCancel() {
                    }

                    override fun onError(error: FacebookException?) {
                    }
                }
            )
        }
    }

    override fun setValues() {
    }
}