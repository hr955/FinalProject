package com.neppplus.gabozago.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.gabozago.FindFriendActivity
import com.neppplus.gabozago.R
import com.neppplus.gabozago.adapters.RequestedFriendListAdapter
import com.neppplus.gabozago.databinding.FragmentRequestedFriendListBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.datas.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestedFriendListFragment : BaseFragment() {

    companion object {
        private var frag: RequestedFriendListFragment? = null

        fun getFrag(): RequestedFriendListFragment {
            if (frag == null) {
                frag = RequestedFriendListFragment()
            }

            return frag!!
        }
    }

    lateinit var binding: FragmentRequestedFriendListBinding
    val mFriendRequestList = ArrayList<UserData>()
    lateinit var mAdapter: RequestedFriendListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        getRequestFriendListFromServer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_requested_friend_list,
                container,
                false
            )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        btnFindFriend.setOnClickListener {
            startActivity(Intent(mContext, FindFriendActivity::class.java))
        }
    }

    override fun setValues() {
        txtTitle.text = "친구"
        btnFindFriend.visibility = View.VISIBLE

        mAdapter = RequestedFriendListAdapter(mContext, mFriendRequestList)
        binding.rvRequestedFriendList.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    fun getRequestFriendListFromServer() {
        apiService.getRequestFriendList("requested").enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful) {
                    mFriendRequestList.clear()
                    mFriendRequestList.addAll(response.body()!!.data.friends)
                    mAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
            }
        })
    }
}