package com.neppplus.gabozago.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.gabozago.EditAppointmentActivity
import com.neppplus.gabozago.R
import com.neppplus.gabozago.adapters.AppointmentListAdapter
import com.neppplus.gabozago.databinding.FragmentMainBinding
import com.neppplus.gabozago.datas.AppointmentData
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.utils.RecyclerViewDecoration
import com.neppplus.gabozago.utils.SizeUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainFragment : BaseFragment() {

    lateinit var binding: FragmentMainBinding
    lateinit var mAdapter: AppointmentListAdapter
    val mAppointmentList = ArrayList<AppointmentData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEvents()
        setValues()
    }

    override fun onResume() {
        super.onResume()

        getAppointmentListFromServer()

    }

    override fun setupEvents() {
        binding.fbAddAppointment.setOnClickListener {
            startActivity(Intent(mContext, EditAppointmentActivity::class.java))
        }
    }

    override fun setValues() {
        mAdapter = AppointmentListAdapter(mContext, mAppointmentList)

        binding.rvAppointmentList.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                RecyclerViewDecoration(
                    SizeUtil.dpToPx(mContext, 0.5f),
                    null,
                    ContextCompat.getColor(mContext, R.color.grey)
                )
            )
        }
    }

    // 서버에서 일정 리스트를 받아와 리사이클러뷰에 뿌려주는 함수
    private fun getAppointmentListFromServer() {
        apiService.getRequestAppointmentList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                mAppointmentList.clear()
                mAppointmentList.addAll(response.body()!!.data.appointments)
                mAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
            }
        })
    }
}