package com.example.finalproject

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivityLoginBinding
import com.facebook.login.LoginResult
import android.content.Intent
import android.util.Log
import com.facebook.*
import com.facebook.login.LoginManager
import com.kakao.sdk.user.UserApiClient
import org.json.JSONObject
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
        binding.btnSignUp.setOnClickListener { 
            startActivity(Intent(mContext, SignUpActivity::class.java))
        }
        
        binding.btnKakaoLogin.setOnClickListener {
            UserApiClient.instance.loginWithKakaoAccount(mContext) { token, error ->
                if (error != null) {
                    Log.e("카카오로그인", "로그인 실패", error)
                }
                else if (token != null) {
                    Log.i("카카오로그인", "로그인 성공 ${token.accessToken}")

                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e("카카오로그인", "사용자 정보 요청 실패", error)
                        }
                        else if (user != null) {
                            Log.i("카카오로그인", "사용자 정보 요청 성공" +
                                    "\n회원번호: ${user.id}" +
                                    "\n이메일: ${user.kakaoAccount?.email}" +
                                    "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                    "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")
                        }
                    }
                }
            }
        }


        callbackManager = CallbackManager.Factory.create()

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

                                    Log.d("이름", name)
                                    Log.d("user_id", id)

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