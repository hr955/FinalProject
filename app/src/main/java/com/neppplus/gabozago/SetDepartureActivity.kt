package com.neppplus.gabozago

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.gabozago.adapters.SetDepartureMyPlaceListAdapter
import com.neppplus.gabozago.databinding.ActivitySetDepartureBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.datas.PlaceData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetDepartureActivity : BaseActivity() {

    lateinit var binding: ActivitySetDepartureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_departure)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
    }

    override fun setValues() {
        getMyPlaceListFromServer { response ->
            binding.rvMyPlaceList.apply {
                adapter = SetDepartureMyPlaceListAdapter(response.data.places)
                layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    private fun getMyPlaceListFromServer(success: (response: BasicResponse) -> Unit){
        apiService.getRequestMyPlaceList().enqueue(object: Callback<BasicResponse>{
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if(response.isSuccessful){
                    success(response.body()!!)
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
            }
        })
    }
}