package com.example.finalproject

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivityLoginBinding
import com.facebook.login.LoginResult
import android.content.Intent
import android.util.Log
import com.facebook.*
import com.facebook.login.LoginManager
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