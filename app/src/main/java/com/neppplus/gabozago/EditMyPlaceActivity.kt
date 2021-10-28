package com.neppplus.gabozago

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neppplus.gabozago.adapters.MyDepartureSearchListAdapter
import com.neppplus.gabozago.adapters.SetPlaceSearchListAdapter
import com.neppplus.gabozago.databinding.ActivityEditMyPlaceBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.datas.Documents
import com.neppplus.gabozago.datas.SearchPlaceData
import com.neppplus.gabozago.web.KakaoAPI
import com.neppplus.gabozago.web.KakaoAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditMyPlaceActivity : BaseActivity() {

    lateinit var binding: ActivityEditMyPlaceBinding

    var mSelectedLat: Double? = null
    var mSelectedLng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_my_place)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        searchDeparture() // 장소 검색
        btnSaveClickEvent() // 저장
    }

    override fun setValues() {

    }

    private fun btnSaveClickEvent() {
        binding.btnSave.setOnClickListener {
            if (binding.edtDepartureNickname.text.isEmpty()) {
                Toast.makeText(mContext, "별칭을 정해주세요", Toast.LENGTH_SHORT).show()
                binding.edtDepartureNickname.requestFocus()
                return@setOnClickListener
            }

            if (mSelectedLat == null || mSelectedLng == null) {
                Toast.makeText(mContext, "주소를 지정해주세요", Toast.LENGTH_SHORT).show()
                binding.edtSearchDeparture.requestFocus()
                return@setOnClickListener
            }

            apiService.postRequestAddMyPlace(
                binding.edtDepartureNickname.text.toString(),
                mSelectedLat!!,
                mSelectedLng!!,
                true
            ).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    finish()
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
            })
        }
    }

    private fun searchDeparture() {
        binding.edtSearchDeparture.setOnEditorActionListener { v, actionId, event ->
            var handled = false

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    binding.edtSearchDeparture.windowToken,
                    0
                )

                KakaoAPI.getRetrofit().getRequestSearchPlace(
                    getString(R.string.kakao_rest_api_key),
                    binding.edtSearchDeparture.text.toString()
                ).enqueue(object : Callback<SearchPlaceData> {
                    override fun onResponse(
                        call: Call<SearchPlaceData>,
                        response: Response<SearchPlaceData>
                    ) {
                        binding.rvSearchList.apply {
                            adapter = MyDepartureSearchListAdapter(mContext, response.body()!!)
                            layoutManager =
                                LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
                        }
                    }

                    override fun onFailure(call: Call<SearchPlaceData>, t: Throwable) {}
                })

                handled = true
            }
            handled
        }
    }
}