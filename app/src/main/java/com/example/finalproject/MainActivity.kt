package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.adapters.AppointmentListAdapter
import com.example.finalproject.databinding.ActivityMainBinding
import com.example.finalproject.datas.AppointmentData
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.utils.GlobalData
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

    override fun onResume() {
        super.onResume()

        getAppointmentListFromServer()
    }

    override fun setupEvents() {
        binding.btnAddAppointment.setOnClickListener {
            startActivity(Intent(mContext, EditAppointmentActivity::class.java))
        }
    }

    override fun setValues() {
        Toast.makeText(mContext, "${GlobalData.loginUser!!.nickname}님 환영합니다!", Toast.LENGTH_SHORT).show()

        mAdapter = AppointmentListAdapter(mContext, mAppointmentList)

        binding.rvAppointmentList.apply{
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

    }

    // 서버에서 일정 리스트를 받아와 리사이클러뷰에 뿌려주는 함수
    fun getAppointmentListFromServer(){
        apiService.getRequestAppointmentList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                Log.d("테스트", response.body().toString())
                mAppointmentList.clear()

                mAppointmentList.addAll(response.body()!!.data.appointments)

                mAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}