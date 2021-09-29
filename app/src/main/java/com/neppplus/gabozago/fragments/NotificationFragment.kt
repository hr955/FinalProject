package com.neppplus.gabozago.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.gabozago.R
import com.neppplus.gabozago.adapters.NotificationAdapter
import com.neppplus.gabozago.databinding.FragmentNotificationBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.datas.NotificationData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationFragment : BaseFragment() {


    lateinit var binding: FragmentNotificationBinding
    val mNotificationList = ArrayList<NotificationData>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
    }

    override fun setValues() {
        txtTitle.text = "알림"

        // 알림 목록 불러오기
        apiService.getRequestNotificationList("true").enqueue(object: Callback<BasicResponse>{
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if(response.isSuccessful){
                    mNotificationList.addAll(response.body()!!.data.notifications)
                    binding.rvNotificationList.apply{
                        adapter = NotificationAdapter(mContext, mNotificationList)
                        layoutManager =
                            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
                    }
                    setNotiIsRead()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
            }
        })
    }

    // 알림 읽음 처리
    fun setNotiIsRead(){
        apiService.postRequestNotiIsRead(mNotificationList[0].id).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                Log.d("NotificationFragment", "NotificationIsRead Success")
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
        })
    }
}