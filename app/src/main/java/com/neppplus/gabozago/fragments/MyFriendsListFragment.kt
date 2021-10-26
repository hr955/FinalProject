package com.neppplus.gabozago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.gabozago.R
import com.neppplus.gabozago.adapters.FriendListAdapter
import com.neppplus.gabozago.databinding.FragmentMyFriendsListBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.datas.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyFriendsListFragment : BaseFragment() {

    companion object {
        private var frag: MyFriendsListFragment? = null

        fun getFrag(): MyFriendsListFragment {
            if (frag == null) {
                frag = MyFriendsListFragment()
            }

            return frag!!
        }
    }

    lateinit var binding: FragmentMyFriendsListBinding
    val mMyFriendList = ArrayList<UserData>()
    lateinit var mFriendListAdapter: FriendListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()

        getMyFriendsListFromServer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_my_friends_list, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {
        mFriendListAdapter = FriendListAdapter(mContext, mMyFriendList)

        binding.rvFriendsList.apply {
            adapter = mFriendListAdapter
            layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    fun getMyFriendsListFromServer() {
        apiService.getRequestFriendList("my").enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful) {
                    mMyFriendList.clear()
                    mMyFriendList.addAll(response.body()!!.data.friends)
                    mFriendListAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
            }
        })
    }
}