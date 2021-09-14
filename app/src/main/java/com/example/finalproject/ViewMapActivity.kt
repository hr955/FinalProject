package com.example.finalproject

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivityViewMapBinding
import com.example.finalproject.datas.AppointmentData
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

class ViewMapActivity : BaseActivity() {

    lateinit var binding: ActivityViewMapBinding
    lateinit var mAppointmentData: AppointmentData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_map)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
    }

    override fun setValues() {
        mAppointmentData = intent.getSerializableExtra("AppointmentData") as AppointmentData

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_naver_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fragment_naver_map, it).commit()
            }

        val coord = LatLng(mAppointmentData.latitude, mAppointmentData.longitude)

        mapFragment.getMapAsync {
            val cameraUpdate = CameraUpdate.scrollTo(coord)
                .animate(CameraAnimation.Easing)
            it.moveCamera(cameraUpdate)

            val selectedPointMarker = Marker(coord)
            selectedPointMarker.icon = OverlayImage.fromResource(R.drawable.ic_pink_marker)
            selectedPointMarker.map = it

        }
    }
}