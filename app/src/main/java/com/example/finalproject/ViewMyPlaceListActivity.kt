package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.adapters.MyPlaceListAdapter
import com.example.finalproject.databinding.ActivityViewMyPlaceListBinding
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.datas.PlaceData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewMyPlaceListActivity : BaseActivity() {

    lateinit var binding: ActivityViewMyPlaceListBinding
    val mMyPlaceList = ArrayList<PlaceData>()
    lateinit var mPlaceAdapter: MyPlaceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_my_place_list)

        setupEvents()
        setValues()
    }

    override fun onResume() {
        super.onResume()
        getMyPlaceListFromServer()
    }

    override fun setupEvents() {
        btnAddPlace.setOnClickListener {
            startActivity(Intent(mContext, EditMyPlaceActivity::class.java))
        }
    }

    override fun setValues() {
        txtTitle.text = "출발지 목록"
        btnAddPlace.visibility = View.VISIBLE

        mPlaceAdapter = MyPlaceListAdapter(mContext, mMyPlaceList)
        binding.rvMyPlaceList.apply {
            adapter = mPlaceAdapter
            layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    fun getMyPlaceListFromServer() {
        apiService.getRequestMyPlaceList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful) {
                    val basicResponse = response.body()!!
                    mMyPlaceList.clear()
                    mMyPlaceList.addAll(basicResponse.data.places)
                    mPlaceAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

    }
}