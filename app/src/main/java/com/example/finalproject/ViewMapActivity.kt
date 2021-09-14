package com.example.finalproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
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
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener

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
        txtTitle.text = "약속장소 확인"

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

            val myODsayService =
                ODsayService.init(mContext, getString(R.string.odsay_app_key))
            myODsayService.requestSearchPubTransPath(
                126.724542.toString(),
                37.489491.toString(),
                mAppointmentData.longitude.toString(),
                mAppointmentData.latitude.toString(),
                null, null, null, object : OnResultCallbackListener {
                    override fun onSuccess(p0: ODsayData?, p1: API?) {
                        val jsonObj = p0!!.json
                        val resultObj = jsonObj.getJSONObject("result")
                        val pathArr = resultObj.getJSONArray("path")

                        val firstPath = pathArr.getJSONObject(0)
                        val infoObj = firstPath.getJSONObject("info")
                        val totalTime = infoObj.getInt("totalTime")
                        Log.d("소요시간", totalTime.toString())

                        val hour = totalTime / 60
                        val minute = totalTime % 60

                        infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(mContext) {
                            override fun getContentView(infoWindow: InfoWindow): View {

                                val myView =
                                    LayoutInflater.from(mContext).inflate(R.layout.my_custom_info_window, null)
                                val placeNameTxt = myView.findViewById<TextView>(R.id.txt_place)
                                val arrivalTimeTxt = myView.findViewById<TextView>(R.id.txt_arrival_time)

                                placeNameTxt.text = mAppointmentData.place
                                arrivalTimeTxt.text = "${hour}시간 ${minute}분 예상"


                                return myView
                            }
                        }
                    }

                    override fun onError(p0: Int, p1: String?, p2: API?) {
                        Log.d("예상시간실패", p1!!)
                    }
                })

            infoWindow.open(marker)

            it.setOnMapClickListener { pointF, latLng ->
                infoWindow.close()
            }

            marker.setOnClickListener {
                val clickedMarker = it as Marker
                if (clickedMarker.infoWindow == null) {
                    infoWindow.open(clickedMarker)
                } else {
                    infoWindow.close()
                }
                return@setOnClickListener true
            }
        }
    }
}