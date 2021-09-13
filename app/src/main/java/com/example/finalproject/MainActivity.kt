package com.example.finalproject

import android.content.Intent
import android.database.DatabaseUtils
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.adapters.AppointmentListAdapter
import com.example.finalproject.databinding.ActivityMainBinding
import com.example.finalproject.datas.AppointmentData

class MainActivity : BaseActivity() {

    lateinit var binding:ActivityMainBinding
    lateinit var mAdapter: AppointmentListAdapter
    val mList = arrayListOf(
        AppointmentData(1,"제목1","일자1", "장소1"),
        AppointmentData(2,"제목2","일자2", "장소2"),
        AppointmentData(3,"제목3","일자3", "장소3")

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        binding.btnAddAppointment.setOnClickListener {
            startActivity(Intent(mContext, EditAppointmentActivity::class.java))
        }
    }

    override fun setValues() {
        mAdapter = AppointmentListAdapter(mList)
        binding.rvAppointmentList.apply{
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

    }
}