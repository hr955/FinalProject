package com.neppplus.gabozago

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.neppplus.gabozago.databinding.ActivityEditMyPlaceBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditMyPlaceActivity : BaseActivity() {

    lateinit var binding: ActivityEditMyPlaceBinding

    var mSelectedLat = 0.0
    var mSelectedLng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_my_place)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.btnSave.setOnClickListener {
            apiService.postRequestAddMyPlace(
                binding.edtPlaceTitle.text.toString(),
                mSelectedLat,
                mSelectedLng,
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

    override fun setValues() {
        txtTitle.text = "출발지 선택"

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_naver_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fragment_naver_map, it).commit()
            }

        mapFragment.getMapAsync {
            Log.d("지도객체 가져오기", "지도객체 가져오기")

            val coord = LatLng(37.497846, 127.027357)

            val cameraUpdate = CameraUpdate.scrollTo(coord)
                .animate(CameraAnimation.Easing)
            it.moveCamera(cameraUpdate)

            val uiSettings = it.uiSettings
            uiSettings.isCompassEnabled = true

            val selectedPointMarker = Marker()
            selectedPointMarker.icon = OverlayImage.fromResource(R.drawable.ic_pink_marker)

            it.setOnMapClickListener { point, coord ->
                Toast.makeText(
                    this, "${coord.latitude}, ${coord.longitude}",
                    Toast.LENGTH_SHORT
                ).show()
                mSelectedLat = coord.latitude
                mSelectedLng = coord.longitude

                selectedPointMarker.position = LatLng(mSelectedLat, mSelectedLng)
                selectedPointMarker.map = it
            }
        }
    }
}