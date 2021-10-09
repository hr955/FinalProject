package com.neppplus.gabozago

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.gabozago.adapters.SetDepartureMyPlaceListAdapter
import com.neppplus.gabozago.adapters.SetDepartureSearchListAdapter
import com.neppplus.gabozago.databinding.ActivitySetDepartureBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.datas.SearchPlaceData
import com.neppplus.gabozago.web.KakaoAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SetDepartureActivity : BaseActivity() {

    lateinit var binding: ActivitySetDepartureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_departure)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        getPlaceSearchList { response ->
            binding.rvDepartureSearchList.apply {
                adapter = SetDepartureSearchListAdapter(response)
                layoutManager =
                    LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    override fun setValues() {
        getMyPlaceListFromServer { response ->
            binding.rvMyPlaceList.apply {
                adapter = SetDepartureMyPlaceListAdapter(response.data.places)
                layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    // 내 출발지 목록 호출
    private fun getMyPlaceListFromServer(success: (response: BasicResponse) -> Unit) {
        apiService.getRequestMyPlaceList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful) {
                    success(response.body()!!)
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
            }
        })
    }

    // 출발지 키워드 검색
    private fun getPlaceSearchList(success: (response: SearchPlaceData) -> Unit) {
        binding.ivSearchDeparture.setOnClickListener {
            val inputPlaceName = binding.edtSearchDeparture.text.toString()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://dapi.kakao.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(KakaoAPIService::class.java)
                .getRequestSearchPlace(getString(R.string.kakao_rest_api_key), inputPlaceName)

            service.enqueue(object : Callback<SearchPlaceData>{
                override fun onResponse(
                    call: Call<SearchPlaceData>,
                    response: Response<SearchPlaceData>
                ) {
                    if(response.isSuccessful){
                        success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<SearchPlaceData>, t: Throwable) {
                }
            })
        }
    }
}