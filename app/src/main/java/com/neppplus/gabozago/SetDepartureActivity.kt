package com.neppplus.gabozago

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.gabozago.adapters.SetDepartureMyPlaceListAdapter
import com.neppplus.gabozago.adapters.SetDepartureSearchListAdapter
import com.neppplus.gabozago.databinding.ActivitySetDepartureBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.datas.PlaceData
import com.neppplus.gabozago.datas.SearchPlaceData
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SetDepartureActivity : BaseActivity() {

    lateinit var binding: ActivitySetDepartureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_departure)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        searchDeparture()
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
    private fun searchDeparture() {
        binding.ivSearchDeparture.setOnClickListener {
            val inputPlaceName = binding.edtSearchDeparture.text.toString()

            if (inputPlaceName.length < 2) {
                Toast.makeText(mContext, "검색어는 2자 이상 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url =
                HttpUrl.parse("https://dapi.kakao.com/v2/local/search/keyword.json")!!.newBuilder()
            url.addQueryParameter("query", inputPlaceName)

            val urlString = url.toString()

            val request = Request.Builder()
                .url(urlString)
                .get()
                .header("Authorization", getString(R.string.kakao_rest_api_key))
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val jsonObj = JSONObject(response.body()!!.string())

                    val documentsArr = jsonObj.getJSONArray("documents")

                    val searchPlaceList = ArrayList<SearchPlaceData>()

                    Log.d("searchPlaceList", documentsArr.length().toString())

                    for (i in 0 until documentsArr.length()) {
                        val docu = documentsArr.getJSONObject(i)
                        val addressName = docu.getString("address_name")
                        val placeName = docu.getString("place_name")
                        val lat = docu.getString("y").toDouble()
                        val lng = docu.getString("x").toDouble()

                        searchPlaceList.add(SearchPlaceData(addressName, placeName, lat, lng))
                    }

                    runOnUiThread {
                        binding.rvDepartureSearchList.apply {
                            adapter = SetDepartureSearchListAdapter(searchPlaceList)
                            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
                        }
                    }
                }
            })
        }
    }
}