package com.example.finalproject

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.adapters.FindFriendListAdapter
import com.example.finalproject.databinding.ActivityFindFriendBinding
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.datas.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindFriendActivity : BaseActivity() {

    lateinit var binding: ActivityFindFriendBinding
    lateinit var mAdapterFind: FindFriendListAdapter
    val mUserSearchList = ArrayList<UserData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_friend)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        binding.btnFind.setOnClickListener {
            if (binding.edtKeyword.text.length < 2) {
                Toast.makeText(mContext, "검색어는 2자 이상 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            getSearchUserListFromServer()
        }
    }

    override fun setValues() {
        txtTitle.text = "친구 검색"

        mAdapterFind = FindFriendListAdapter(mContext, mUserSearchList)

        binding.rvFindList.apply {
            adapter = mAdapterFind
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    fun getSearchUserListFromServer() {
        apiService.getRequestSearchUser(binding.edtKeyword.text.toString()).enqueue(object :
            Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful) {
                    mUserSearchList.clear()
                    mUserSearchList.addAll(response.body()!!.data.users)
                    mAdapterFind.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
            }
        })
    }
}