package com.example.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivityViewMapBinding
import com.example.finalproject.datas.AppointmentData
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.InfoWindow
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

            val marker = Marker(coord)
            marker.icon = OverlayImage.fromResource(R.drawable.ic_pink_marker)
            marker.map = it

            val infoWindow = InfoWindow()
            infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(mContext) {
                override fun getContentView(infoWindow: InfoWindow): View {

                    val myView =
                        LayoutInflater.from(mContext).inflate(R.layout.my_custom_info_window, null)
                    val placeNameTxt = myView.findViewById<TextView>(R.id.txt_place)
                    val arrivalTimeTxt = myView.findViewById<TextView>(R.id.txt_arrival_time)

                    placeNameTxt.text = mAppointmentData.place
                    arrivalTimeTxt.text = "4시간 예상"

                    return myView
                }
            }
            infoWindow.open(marker)

            it.setOnMapClickListener { pointF, latLng ->
                infoWindow.close()
            }

            marker.setOnClickListener {
                val clickedMarker = it as Marker
                if(clickedMarker.infoWindow == null){
                    infoWindow.open(clickedMarker)
                }else{
                    infoWindow.close()
                }
                return@setOnClickListener true
            }
        }
    }
}