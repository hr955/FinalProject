package com.neppplus.gabozago

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.neppplus.gabozago.databinding.ActivityViewAppointmentDetailBinding
import com.neppplus.gabozago.datas.AppointmentData
import com.neppplus.gabozago.datas.BasicResponse
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.MarkerIcons
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class ViewAppointmentDetailActivity : BaseActivity() {

    lateinit var binding: ActivityViewAppointmentDetailBinding
    lateinit var mAppointmentData: AppointmentData

    var needLocationSendServer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_appointment_detail)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        // 친구목록 새로고침 (도착여부 확인)
        binding.btnRefresh.setOnClickListener {
            getAppointmentFromServer()
        }

        // 약속 수정
        binding.btnEditAppointment.setOnClickListener {
            editAppointment()
        }

        // 도착 인증 버튼
        binding.btnArrival.setOnClickListener {

            // 서버에 위치를 보내야한다고 flag값을 true
            needLocationSendServer = true
            Log.d("테스트1", "테스트1")

            val pl = object : PermissionListener {
                override fun onPermissionGranted() {
                    if (ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        return
                    }

                    val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0L,
                        0f,
                        object : LocationListener {
                            override fun onLocationChanged(p0: Location) {
                                if (needLocationSendServer) {
                                    // 서버에 위경도값 보내주기.
                                    apiService.postRequestArrival(
                                        mAppointmentData.id,
                                        p0.latitude,
                                        p0.longitude
                                    ).enqueue(object : Callback<BasicResponse> {
                                        override fun onResponse(
                                            call: Call<BasicResponse>,
                                            response: Response<BasicResponse>
                                        ) {
                                            if (response.isSuccessful) {
                                                needLocationSendServer = false
                                                Toast.makeText(
                                                    mContext,
                                                    "약속 인증에 성공했습니다.",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            } else {
                                                val jsonObj =
                                                    JSONObject(response.errorBody()!!.string())
                                                Log.d("응답전문", jsonObj.toString())

                                                val message = jsonObj.getString("message")

                                                Toast.makeText(
                                                    mContext,
                                                    message,
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()

                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<BasicResponse>,
                                            t: Throwable
                                        ) {
                                        }
                                    })
//                                  응답이 성공적으로 돌아오면 => 서버에 안보내기.
                                }
                            }

                            override fun onStatusChanged(
                                provider: String?,
                                status: Int,
                                extras: Bundle?
                            ) {

                            }

                            override fun onProviderEnabled(provider: String) {

                            }

                            override fun onProviderDisabled(provider: String) {

                            }
                        })
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(mContext, "현재 위치 정보를 파악해야 약속 도착 인증이 가능합니다.", Toast.LENGTH_SHORT)
                        .show()
                }

            }
            TedPermission.create()
                .setPermissionListener(pl)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check()
        }

        // 약속 삭제
        deleteAppointment()


    }

    override fun setValues() {
        setAppointmentData(intent.getSerializableExtra("AppointmentData") as AppointmentData)
    }

    // 약속 데이터 설정
    private fun setAppointmentData(appointmentData: AppointmentData) {
        mAppointmentData = appointmentData

        binding.txtTitle.text = mAppointmentData.title
        binding.txtPlace.text = mAppointmentData.place

        binding.txtFriendCount.text = "( 참여 인원 : ${mAppointmentData.invitedFriendList.size}명 )"

        binding.txtDate.text = mAppointmentData.getFormattedDateTime()
        setArrivalMarker()

        getAppointmentFromServer()
    }

    // 서버에서 친구 도착정보 가져오기
    private fun getAppointmentFromServer() {
        apiService.getRequestAppointmentDetail(mAppointmentData.id)
            .enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    val basicResponse = response.body()!!

                    mAppointmentData = basicResponse.data.appointment
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
            })

        binding.layoutFriendList.removeAllViews()

        val inflater = LayoutInflater.from(mContext)

        val sdf = SimpleDateFormat("H:mm 도착")

        for (friend in mAppointmentData.invitedFriendList) {
            val friendView = inflater.inflate(R.layout.item_invited_friends_list, null)
            val ivFriendProfile = friendView.findViewById<ImageView>(R.id.iv_friend_profile)
            val txtFriendNickname = friendView.findViewById<TextView>(R.id.txt_friend_nickname)
            val txtArrivalStatus = friendView.findViewById<TextView>(R.id.txt_arrival_status)

            if (friend.arrivedAt == null) {
                txtArrivalStatus.text = "도착 전"
            } else {
                txtArrivalStatus.text = sdf.format(friend.arrivedAt)
            }

            Glide.with(mContext).load(friend.profileImgURL).into(ivFriendProfile)
            txtFriendNickname.text = friend.nickname

            binding.layoutFriendList.addView(friendView)

        }
    }

    private fun setArrivalMarker() {
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
        arrivalMarker.icon = MarkerIcons.BLACK
        arrivalMarker.iconTintColor = Color.RED

        mapFragment.getMapAsync {
            val cameraUpdate = CameraUpdate.scrollTo(arrivalPosition)
                .animate(CameraAnimation.Easing)
            it.moveCamera(cameraUpdate)

            startPlaceMarker.map = it
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

    // 약속 삭제
    private fun deleteAppointment() {
        binding.btnDeleteAppointment.setOnClickListener {
            val alert = AlertDialog.Builder(mContext)
            alert.setTitle("약속을 삭제하시겠습니까?")
            alert.setPositiveButton("삭제", DialogInterface.OnClickListener { dialogInterface, i ->
                apiService.deleteRequestAppointment(mAppointmentData.id)
                    .enqueue(object : Callback<BasicResponse> {
                        override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                            if (response.isSuccessful) Log.d("deleteAppointment", "Delete Appointment Successful")
                        }

                        override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
                    })
                finish()
            })
            alert.setNegativeButton("취소", null)
            alert.show()
        }
    }

    // 약속 수정
    private fun editAppointment(){
        val myIntent = Intent(mContext, EditAppointmentActivity::class.java)
        myIntent.putExtra("AppointmentData", mAppointmentData)
        myIntent.putExtra("EditMode", true)
        startForEditAppointment.launch(myIntent)

    }

    private val startForEditAppointment: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode == RESULT_OK){
                setAppointmentData(result.data!!.getSerializableExtra("AppointmentData") as AppointmentData)
            }
        }
}