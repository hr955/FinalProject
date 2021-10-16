package com.neppplus.gabozago

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.gabozago.adapters.SetDepartureMyPlaceListAdapter
import com.neppplus.gabozago.adapters.SetPlaceSearchListAdapter
import com.neppplus.gabozago.databinding.ActivitySetDepartureBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.datas.SearchPlaceData
import com.neppplus.gabozago.web.KakaoAPI
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
        getPlaceSearchList { response ->
            binding.rvDepartureSearchList.apply {
                adapter = SetPlaceSearchListAdapter(mContext, response)
                layoutManager =
                    LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    override fun setValues() {
        getMyPlaceListFromServer { response ->
            binding.rvMyPlaceList.apply {
                adapter = SetDepartureMyPlaceListAdapter(mContext, response.data.places)
                layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
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

            val service = KakaoAPI.getRetrofit()
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