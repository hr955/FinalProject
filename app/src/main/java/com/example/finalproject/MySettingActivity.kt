package com.example.finalproject

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.finalproject.databinding.ActivityMySettingBinding
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.utils.ContextUtil
import com.example.finalproject.utils.GlobalData
import com.example.finalproject.utils.URIPathHelper
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MySettingActivity : BaseActivity() {

    lateinit var binding: ActivityMySettingBinding
    val REQ_FOR_GALLERY = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_setting)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        binding.layoutFriendsList.setOnClickListener {
            startActivity(Intent(mContext, ViewMyFriendsListActivity::class.java))
        }

        binding.ivProfile.setOnClickListener {
            val permissionListener = object : PermissionListener {
                override fun onPermissionGranted() {
                    val myIntent = Intent()
                    myIntent.action = Intent.ACTION_PICK
                    myIntent.type = "image/*"
                    myIntent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
                    startActivityForResult(myIntent, REQ_FOR_GALLERY)
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(mContext, "접근 권한이 필요합니다", Toast.LENGTH_SHORT).show()
                }
            }

            TedPermission.create()
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .setDeniedMessage("[설정] > [권한]에서 접근을 허용해주세요")
                .check()
        }

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
            val alert = AlertDialog.Builder(mContext)
            alert.setTitle("정말 로그아웃 하시겠습니까?")
            alert.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->
                ContextUtil.setToken(mContext, "")
                GlobalData.loginUser = null
                val myIntent = Intent(mContext, LoginActivity::class.java)
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(myIntent)
                finish()
            })
            alert.setNegativeButton("취소", null)
            alert.show()
        }
    }

    override fun setValues() {
        txtTitle.text = "프로필"
        Log.d("프로바이더", GlobalData.loginUser!!.provider)

        binding.ivLoginSocialLogo.apply {
            when (GlobalData.loginUser!!.provider) {
                "facebook" -> {
                    setImageResource(R.drawable.ic_facebook_logo_color)
                    binding.layoutChangePassword.visibility = View.GONE
                }
                "kakao" -> {
                    setImageResource(R.drawable.ic_kakao_logo)
                    binding.layoutChangePassword.visibility = View.GONE
                }
                else -> visibility = View.GONE
            }
        }

        setUserInfo()
    }

    fun patchUserInfo(title: String, field: String) {
        val et = EditText(mContext)
        et.setPadding(50, 0, 50, 0)

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
        alert.setNegativeButton("취소", null)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_FOR_GALLERY) {
            if (resultCode == RESULT_OK) {

                val dataUri = data?.data
                val file = File(URIPathHelper().getPath(mContext, dataUri!!))
                val fileReqBody = RequestBody.create(MediaType.get("image/*"), file)
                val body = MultipartBody.Part.createFormData(
                    "profile_image",
                    "myFile.jpg",
                    fileReqBody
                )

                apiService.putRequestProfileImage(body)
                    .enqueue(object : Callback<BasicResponse> {
                        override fun onResponse(
                            call: Call<BasicResponse>,
                            response: Response<BasicResponse>
                        ) {
                            Log.d("프사선택1",response.message())

                            if(response.isSuccessful){
                                Glide.with(mContext).load(dataUri).into(binding.ivProfile)
                                GlobalData.loginUser!!.profileImgURL = dataUri.toString()
                            }
                        }

                        override fun onFailure(call: Call<BasicResponse>, t: Throwable) { }
                    })
            }
        }
    }
}