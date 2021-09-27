package com.example.finalproject.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.adapters.NotificationAdapter
import com.example.finalproject.databinding.FragmentNotificationBinding
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.datas.NotificationData
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

        apiService.getRequestNotificationList("true").enqueue(object: Callback<BasicResponse>{
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                mNotificationList.addAll(response.body()!!.data.notifications)
                binding.rvNotificationList.apply{
                    adapter = NotificationAdapter(mContext, mNotificationList)
                    layoutManager =
                        LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
            }
        })
    }
}