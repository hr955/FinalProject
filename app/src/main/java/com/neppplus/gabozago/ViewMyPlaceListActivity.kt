package com.neppplus.gabozago

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.gabozago.adapters.MyPlaceListAdapter
import com.neppplus.gabozago.databinding.ActivityViewMyPlaceListBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.datas.PlaceData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewMyPlaceListActivity : com.neppplus.gabozago.BaseActivity() {

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
        binding.btnAddMyDeparture.setOnClickListener {
            startActivity(Intent(mContext, EditMyPlaceActivity::class.java))
        }
    }

    override fun setValues() {
        mPlaceAdapter = MyPlaceListAdapter(mContext, mMyPlaceList)
        binding.rvMyDepartureList.apply {
            adapter = mPlaceAdapter
            layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun getMyPlaceListFromServer() {
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