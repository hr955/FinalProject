package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.adapters.AppointmentListAdapter
import com.example.finalproject.databinding.ActivityMainBinding
import com.example.finalproject.datas.AppointmentData
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.fragments.InvitationsFragment
import com.example.finalproject.fragments.MainFragment
import com.example.finalproject.fragments.SettingFragment
import com.example.finalproject.utils.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {

    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

//        binding.btnAddAppointment.setOnClickListener {
//            startActivity(Intent(mContext, EditAppointmentActivity::class.java))
//        }

        btnProfile.setOnClickListener {
            startActivity(Intent(mContext, MySettingActivity::class.java))
        }
    }

    override fun setValues() {
        replaceFragment(MainFragment())

        binding.navBarBottom.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.menu_my_appointment -> {
                    replaceFragment(MainFragment())
                }
                R.id.menu_invite_appointment -> {
                    replaceFragment(InvitationsFragment())
                }
                R.id.menu_setting -> {
                    replaceFragment(SettingFragment())
                }
                else -> { }
            }
            true
        }

        Toast.makeText(mContext, "${GlobalData.loginUser!!.nickname}님 환영합니다!", Toast.LENGTH_SHORT).show()

        btnProfile.visibility = View.VISIBLE
        txtTitle.text = "메인 화면"

    }

    fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout_main, fragment)
        fragmentTransaction.commit()
    }
}