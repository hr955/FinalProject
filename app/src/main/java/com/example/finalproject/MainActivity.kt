package com.example.finalproject

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.finalproject.databinding.ActivityMainBinding
import com.example.finalproject.fragments.*
import com.example.finalproject.utils.GlobalData

class MainActivity : BaseActivity() {

    lateinit var binding:ActivityMainBinding

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

        Toast.makeText(mContext, "${GlobalData.loginUser!!.nickname}님 환영합니다!", Toast.LENGTH_SHORT).show()
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout_main, fragment)
        fragmentTransaction.commit()
    }
}