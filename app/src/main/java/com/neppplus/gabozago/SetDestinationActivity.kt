package com.neppplus.gabozago

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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

        // 도착지 검색 (검색 버튼)
        binding.ivSearchDestination.setOnClickListener {
            setSearchListAdapter()
        }

        // 도착지 검색 (키보드 검색 버튼)
        keyboardSearchBtn()

    }

    override fun setValues() {

    }

    // 키보드 검색버튼
    private fun keyboardSearchBtn() {
        binding.edtSetDestination.setOnEditorActionListener { v, actionId, event ->
            var handled = false

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    binding.edtSetDestination.windowToken,
                    0
                )

                setSearchListAdapter()

                handled = true
            }

            handled
        }
    }

    // 어댑터 연결
    private fun setSearchListAdapter() {
        getPlaceSearchList { response ->
            binding.rvDestinationSearchList.apply {
                adapter = SetPlaceSearchListAdapter(mContext, response)
                layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    // 도착지 검색 API 호출
    private fun getPlaceSearchList(success: (response: SearchPlaceData) -> Unit) {
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