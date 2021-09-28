package com.example.finalproject

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.finalproject.databinding.ActivityMainBinding
import com.example.finalproject.fragments.*
import com.example.finalproject.utils.GlobalData
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : BaseActivity() {

    lateinit var binding:ActivityMainBinding
    lateinit var mBottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupEvents()
        setValues()
    }

    override fun setupEvents() { }

    override fun setValues() {
        replaceFragment(MainFragment())

        binding.navBarBottom.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.menu_my_appointment -> replaceFragment(MainFragment())
                R.id.menu_invite_appointment -> replaceFragment(InvitationsFragment())
                R.id.menu_setting -> replaceFragment(SettingFragment())
                R.id.menu_friend -> replaceFragment(FriendFragment())
                R.id.menu_notification -> replaceFragment(NotificationFragment())
                else -> { }
            }
            true
        }

        setNotificationBadge()

        Toast.makeText(mContext, "${GlobalData.loginUser!!.nickname}님 환영합니다!", Toast.LENGTH_SHORT).show()
    }

    private fun setNotificationBadge(){
        binding.navBarBottom.getOrCreateBadge(R.layout.my_custom_notification_badge).number = 3
//        apiService.getRequestNotificationList("false").enqueue(object : Callback<BasicResponse>{
//            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
//
//
//            }
//
//            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
//            }
//        })
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout_main, fragment)
        fragmentTransaction.commit()
    }
}