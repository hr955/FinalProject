package com.neppplus.gabozago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.gabozago.R
import com.neppplus.gabozago.adapters.InvitedAppointmentListAdapter
import com.neppplus.gabozago.databinding.FragmentInvitationBinding
import com.neppplus.gabozago.datas.AppointmentData
import com.neppplus.gabozago.datas.BasicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InvitationsFragment : BaseFragment() {

    lateinit var binding: FragmentInvitationBinding
    val mAppointmentList = ArrayList<AppointmentData>()
    lateinit var mAdapter: InvitedAppointmentListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invitation, container, false)

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
        txtTitle.text = "초대받은 약속"

        apiService.getRequestAppointmentList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful) {
                    mAppointmentList.addAll(response.body()!!.data.invitedAppointments)
                    mAdapter = InvitedAppointmentListAdapter(mContext, mAppointmentList)
                    binding.rvInvitedAppointmentList.apply {
                        adapter = mAdapter
                        layoutManager =
                            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
                    }
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
        })
    }
}