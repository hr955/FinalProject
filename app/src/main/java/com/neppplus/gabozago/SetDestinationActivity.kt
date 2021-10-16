package com.neppplus.gabozago

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.gabozago.adapters.SetPlaceSearchListAdapter
import com.neppplus.gabozago.databinding.ActivitySetDestinationBinding
import com.neppplus.gabozago.datas.SearchPlaceData
import com.neppplus.gabozago.web.KakaoAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetDestinationActivity : BaseActivity() {

    lateinit var binding: ActivitySetDestinationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_destination)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        binding.btnClose.setOnClickListener { finish() }

        // 도착지 검색
        getPlaceSearchList { response ->
            binding.rvDestinationSearchList.apply {
                adapter = SetPlaceSearchListAdapter(response)
                layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    override fun setValues() {
    }

    // 도착지 검색
    private fun getPlaceSearchList(success: (response: SearchPlaceData) -> Unit) {
        binding.ivSearchDestination.setOnClickListener {
            val inputPlaceName = binding.edtSetDestination.text.toString()

            val service = KakaoAPI.getRetrofit()
                .getRequestSearchPlace(getString(R.string.kakao_rest_api_key), inputPlaceName)

            service.enqueue(object : Callback<SearchPlaceData> {
                override fun onResponse(
                    call: Call<SearchPlaceData>,
                    response: Response<SearchPlaceData>
                ) {
                    if (response.isSuccessful) {
                        success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<SearchPlaceData>, t: Throwable) {}
            })
        }
    }
}