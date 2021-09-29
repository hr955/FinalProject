package com.neppplus.gabozago.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.neppplus.gabozago.*
import com.neppplus.gabozago.databinding.FragmentSettingBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.utils.ContextUtil
import com.neppplus.gabozago.utils.GlobalData
import com.neppplus.gabozago.utils.URIPathHelper
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

// TODO alertDialog 커스텀

class SettingFragment : BaseFragment() {
    lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        profileImageChangeButtonClickEvent()
        logoutButtonClickEvent()

        // 닉네임 변경
        binding.btnEditNickname.setOnClickListener {
            patchUserInfo("닉네임 입력", "nickname")
        }

        // 준비 시간 수정
        binding.layoutReadyTime.setOnClickListener {
            patchUserInfo("준비시간 입력", "ready_minute")
        }

        // 출발지 목록
        binding.layoutMyPlaceList.setOnClickListener {
            startActivity(Intent(mContext, ViewMyPlaceListActivity::class.java))
        }

        // 비밀번호 변경
        binding.layoutPasswordChange.setOnClickListener {
            startActivity(Intent(mContext, ChangePasswordActivity::class.java))
        }
    }

    override fun setValues() {
        txtTitle.text = "설정"

        setUserInfo()
    }

    // 로그아웃 버튼
    private fun logoutButtonClickEvent() {
        binding.layoutLogout.setOnClickListener {
            val alert = AlertDialog.Builder(mContext)
            alert.setTitle("정말 로그아웃 하시겠습니까?")
            alert.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->
                ContextUtil.setToken(mContext, "")
                GlobalData.loginUser = null

                val myIntent = Intent(mContext, LoginActivity::class.java)
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(myIntent)
                (activity as MainActivity).finish()
            })
            alert.setNegativeButton("취소", null)
            alert.show()
        }
    }

    // 프로필사진 변경 (갤러리로 이동)
    private fun profileImageChangeButtonClickEvent() {

        val changeProfileImgBtnClickListener  = object: View.OnClickListener{
            override fun onClick(p0: View?) {
                val permissionListener = object : PermissionListener {
                    override fun onPermissionGranted() {
                        val myIntent = Intent()
                        myIntent.action = Intent.ACTION_PICK
                        myIntent.type = "image/*"
                        myIntent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE

                        startForSelectedImageResult.launch(myIntent)
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
        }

        binding.ivProfileImage.setOnClickListener(changeProfileImgBtnClickListener)
        binding.ivChangeProfileImage.setOnClickListener(changeProfileImgBtnClickListener)
    }

    // 사용자 정보 설정
    fun setUserInfo() {
        val loginUser = GlobalData.loginUser!!

        Glide.with(mContext).load(loginUser.profileImgURL).into(binding.ivProfileImage)

        binding.txtNickname.text = loginUser.nickname

        binding.ivLoginSocialLogo.apply {
            when (GlobalData.loginUser!!.provider) {
                "facebook" -> {
                    setImageResource(R.drawable.ic_facebook_logo_color)
                    binding.layoutPasswordChange.visibility = View.GONE
                    binding.viewBorder.visibility = View.GONE
                }
                "kakao" -> {
                    setImageResource(R.drawable.ic_kakao_logo)
                    binding.layoutPasswordChange.visibility = View.GONE
                    binding.viewBorder.visibility = View.GONE
                }
                else -> visibility = View.GONE
            }
        }

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

    // 준비시간, 닉네임 수정
    private fun patchUserInfo(title: String, field: String) {
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

    // 갤러리에서 이미지 선택후 실행될 동작
    private val startForSelectedImageResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val dataUri = result.data?.data
                val file = File(URIPathHelper().getPath(mContext, dataUri!!)!!)
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
                            if (response.isSuccessful) {
                                Glide.with(mContext).load(dataUri).into(binding.ivProfileImage)
                                GlobalData.loginUser!!.profileImgURL = dataUri.toString()
                            }
                        }

                        override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
                    })
            }
        }
}