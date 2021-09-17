package com.example.finalproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.adapters.FriendsListAdapter
import com.example.finalproject.databinding.FragmentMyFriendsListBinding
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.datas.UserData
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
    lateinit var mFriendsListAdapter: FriendsListAdapter

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
        mFriendsListAdapter = FriendsListAdapter(mContext, mMyFriendList)

        binding.rvFriendsList.apply {
            adapter = mFriendsListAdapter
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
                    mFriendsListAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
            }
        })
    }
}