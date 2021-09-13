package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.adapters.AppointmentListAdapter
import com.example.finalproject.databinding.ActivityMainBinding
import com.example.finalproject.datas.AppointmentData
import com.example.finalproject.datas.BasicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {

    lateinit var binding:ActivityMainBinding
    lateinit var mAdapter: AppointmentListAdapter
    val mAppointmentList = ArrayList<AppointmentData>()

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
        getAppointmentListFromServer()
    }

    fun getAppointmentListFromServer(){
        apiService.getRequestAppointmentList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                mAppointmentList.addAll(response.body()!!.data.appointments)

                mAdapter = AppointmentListAdapter(mAppointmentList)
                binding.rvAppointmentList.apply{
                    adapter = mAdapter
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}