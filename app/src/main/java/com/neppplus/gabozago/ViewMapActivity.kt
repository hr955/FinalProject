package com.neppplus.gabozago

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.neppplus.gabozago.databinding.ActivityViewMapBinding
import com.neppplus.gabozago.datas.AppointmentData
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
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

        drawMarkerAndPath()

    }

    // 출발지점, 도착지점 마커 및 안내창 띄우기
    fun drawMarkerAndPath() {
        val fm = supportFragmentManager
        val infoWindow = InfoWindow()
        val path = PathOverlay()

        val mapFragment = fm.findFragmentById(R.id.fragment_naver_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fragment_naver_map, it).commit()
            }

        val startPlaceMarker = Marker()
        val startLatitude = mAppointmentData.startlatitude
        val startLongitude = mAppointmentData.startlongitude
        val startPosition = LatLng(startLatitude, startLongitude)
        startPlaceMarker.position = startPosition

        val arrivalMarker = Marker()
        val arrivalLatitude = mAppointmentData.latitude
        val arrivalLongitude = mAppointmentData.longitude
        val arrivalPosition = LatLng(arrivalLatitude, arrivalLongitude)
        arrivalMarker.position = arrivalPosition

        mapFragment.getMapAsync {
            val cameraUpdate = CameraUpdate.scrollTo(arrivalPosition)
                .animate(CameraAnimation.Easing)
            it.moveCamera(cameraUpdate)

            startPlaceMarker.icon = OverlayImage.fromResource(R.drawable.ic_pink_marker)
            startPlaceMarker.map = it

            arrivalMarker.icon = OverlayImage.fromResource(R.drawable.ic_pink_marker)
            arrivalMarker.map = it

            val points = ArrayList<LatLng>()
            points.add(startPosition)

            val odsay = ODsayService.init(mContext, getString(R.string.odsay_app_key))
            odsay.requestSearchPubTransPath(
                startLongitude.toString(),
                startLatitude.toString(),
                arrivalLongitude.toString(),
                arrivalLatitude.toString(),
                null,
                null,
                null,
                object : OnResultCallbackListener {
                    override fun onSuccess(p0: ODsayData?, p1: API?) {
                        val jsonObj = p0!!.json
                        val resultObj = jsonObj.getJSONObject("result")
                        val pathArr = resultObj.getJSONArray("path")
                        val firstPathObj = pathArr.getJSONObject(0)

                        val infoObj = firstPathObj.getJSONObject("info")
                        val totalTime = infoObj.getInt("totalTime")

                        val hour = totalTime / 60
                        val minute = totalTime % 60

                        infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(mContext) {
                            override fun getContentView(infoWindow: InfoWindow): View {
                                val myView =
                                    LayoutInflater.from(mContext)
                                        .inflate(R.layout.my_custom_info_window, null)
                                val placeNameTxt = myView.findViewById<TextView>(R.id.txt_place)
                                val arrivalTimeTxt =
                                    myView.findViewById<TextView>(R.id.txt_arrival_time)

                                placeNameTxt.text = mAppointmentData.place

                                if (hour == 0) {
                                    arrivalTimeTxt.text = "${minute}분 예상"
                                } else {
                                    if (minute == 0) {
                                        arrivalTimeTxt.text = "${hour}시간 예상"
                                    } else {
                                        arrivalTimeTxt.text = "${hour}시간 ${minute}분 예상"
                                    }
                                }
                                return myView
                            }
                        }
                        infoWindow.open(arrivalMarker)

                        val subPathArr = firstPathObj.getJSONArray("subPath")
                        for (i in 0 until subPathArr.length()) {
                            val subPathObj = subPathArr.getJSONObject(i)
                            if (!subPathObj.isNull("passStopList")) {
                                val passStopListObj = subPathObj.getJSONObject("passStopList")
                                val stationArr = passStopListObj.getJSONArray("stations")
                                for (j in 0 until stationArr.length()) {
                                    val stationObj = stationArr.getJSONObject(j)
                                    points.add(
                                        LatLng(
                                            stationObj.getString("y").toDouble(),
                                            stationObj.getString("x").toDouble()
                                        )
                                    )
                                }
                            }
                        }
                        points.add(LatLng(arrivalLatitude, arrivalLongitude))

                        path.coords = points
                        path.map = it
                    }

                    override fun onError(p0: Int, p1: String?, p2: API?) {
                    }
                }
            )
        }
    }
}