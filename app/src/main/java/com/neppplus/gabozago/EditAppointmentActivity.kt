package com.neppplus.gabozago

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.neppplus.gabozago.adapters.AddFriendSpinnerAdapter
import com.neppplus.gabozago.databinding.ActivityEditAppointmentBinding
import com.neppplus.gabozago.datas.*
import com.neppplus.gabozago.utils.SizeUtil
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditAppointmentActivity : BaseActivity() {

    lateinit var binding: ActivityEditAppointmentBinding

    private val mSelectedDateTime = Calendar.getInstance()

    val mFriendList = ArrayList<UserData>()
    lateinit var mAddFriendSpinnerAdapter: AddFriendSpinnerAdapter
    private val mSelectedFriendList = ArrayList<UserData>()

    var mDepartureData = Documents("null", "null", 0.0, 0.0)
    var mDestinationData = Documents("null", "null", 0.0, 0.0)

    private val mDepartureMarker = Marker()
    private val mDepartureInfoWindow = InfoWindow()

    private val mDestinationMarker = Marker()
    private val mDestinationInfoWindow = InfoWindow()

    val mPath = PathOverlay()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appointment)

        setupEvents()
        setValues()

    }

    override fun setupEvents() {
        // 뒤로가기 버튼
        binding.btnClose.setOnClickListener {
            finish()
        }

        dateSelectButtonClickEvent() // 날짜 설정
        timeSelectButtonClickEvent() // 시간 설정
        addFriendButtonClickEvent() // 약속에 초대할 친구 추가
        setDeparture() // 출발지 설정
        setDestination() // 도착지 설정

        // 지도영역이 터치되면 스크롤뷰 정지
        binding.txtScrollHelp.setOnTouchListener { view, motionEvent ->
            binding.scrollview.requestDisallowInterceptTouchEvent(true)
            return@setOnTouchListener false
        }

//        saveButtonClickEvent()
    }

    override fun setValues() {
        getMyFriendListFromServer() // 친구 목록 불러오기

        mAddFriendSpinnerAdapter =
            AddFriendSpinnerAdapter(mContext, R.layout.item_spinner_friend_list, mFriendList)
        binding.spinnerFriendList.adapter = mAddFriendSpinnerAdapter

    }

    // 친구 목록 불러오기
    private fun getMyFriendListFromServer() {
        apiService.getRequestFriendList("my").enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                mFriendList.clear()
                mFriendList.addAll(response.body()!!.data.friends)

                mAddFriendSpinnerAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
        })
    }

    // 날짜 선택 버튼
    private fun dateSelectButtonClickEvent() {
        binding.txtSelectDate.setOnClickListener {
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    mSelectedDateTime.set(year, month, dayOfMonth)
                    binding.txtSelectDate.text =
                        SimpleDateFormat("yyyy. M. d (E)").format(mSelectedDateTime.time)
                }

            val datePicker = DatePickerDialog(
                mContext,
                dateSetListener,
                mSelectedDateTime.get(Calendar.YEAR),
                mSelectedDateTime.get(Calendar.MONTH),
                mSelectedDateTime.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePicker.show()
        }
    }

    // 시간 선택 버튼
    private fun timeSelectButtonClickEvent() {
        binding.txtSelectTime.setOnClickListener {
            val timeSetListener =
                TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
                    mSelectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    mSelectedDateTime.set(Calendar.MINUTE, minute)

                    binding.txtSelectTime.text =
                        SimpleDateFormat("a hh : mm").format(mSelectedDateTime.time)
                }
            val timepicker = TimePickerDialog(
                this,
                timeSetListener,
                mSelectedDateTime.get(Calendar.HOUR_OF_DAY),
                mSelectedDateTime.get(Calendar.MINUTE),
                false
            )
            timepicker.setTitle("시간 선택")
            timepicker.show()
        }
    }

    // 초대할 친구 추가 및 삭제
    private fun addFriendButtonClickEvent() {
        binding.btnAddFriend.setOnClickListener {

            val selectedFriend = mFriendList[binding.spinnerFriendList.selectedItemPosition]

            if (mSelectedFriendList.contains(selectedFriend)) {
                Toast.makeText(mContext, "이미 추가된 친구입니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val inflater =
                LayoutInflater.from(mContext).inflate(R.layout.item_add_friend_list, null)
            val layout = inflater.findViewById<LinearLayout>(R.id.linear_layout)
            val txtFriendNickname = inflater.findViewById<TextView>(R.id.txt_friend_nickname)
            val btnDelete = inflater.findViewById<ImageView>(R.id.btn_delete)

            val param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            param.marginEnd = SizeUtil.dpToPx(mContext, 5f).toInt()
            layout.layoutParams = param

            txtFriendNickname.text = selectedFriend.nickname

            mSelectedFriendList.add(selectedFriend)
            binding.flAddFriend.addView(layout)

            btnDelete.setOnClickListener {
                binding.flAddFriend.removeView(layout)
                mSelectedFriendList.remove(selectedFriend)
            }
        }
    }

    // 출발지 설정 & Map Marker 표시
    private fun setDeparture() {
        binding.viewDeparture.setOnClickListener {
            startForSetDeparture.launch(Intent(this, SetDepartureActivity::class.java))
        }
    }

    private val startForSetDeparture: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {

                // 내 출발지 목록에서 선택했을때 or 검색하여 선택했을때
                when (result.data!!.getStringExtra("DepartureType")) {
                    "MyPlaceList" -> {
                        val myData =
                            result.data!!.getSerializableExtra("DepartureData") as PlaceData
                        binding.txtSelectDeparture.text = "출발지 | ${myData?.name}"
                        binding.txtDepartureWarning.visibility = View.GONE
                        mDepartureData.apply {
                            placeName = myData.name
                            latitude = myData.latitude
                            longitude = myData.longitude
                        }
                    }
                    "SearchList" -> {
                        val myData =
                            result.data!!.getSerializableExtra("SearchPlaceData") as Documents
                        binding.txtSelectDeparture.text = "출발지 | ${myData.placeName}"
                        binding.txtDepartureWarning.visibility = View.GONE
                        mDepartureData = myData
                    }
                }

                setDepartureMarker()
            }
        }

    // 출발지 마커
    private fun setDepartureMarker() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_naver_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fragment_naver_map, it).commit()
            }
        mapFragment.getMapAsync {
            val departurePosition = LatLng(mDepartureData.latitude, mDepartureData.longitude)

            val cameraUpdate = CameraUpdate.scrollTo(departurePosition)
            it.moveCamera(cameraUpdate)

            mDepartureMarker.position = departurePosition
            mDepartureMarker.map = it

            mDepartureInfoWindow.adapter = object : InfoWindow.DefaultViewAdapter(mContext) {
                override fun getContentView(p0: InfoWindow): View {
                    val view =
                        LayoutInflater.from(mContext).inflate(R.layout.my_custom_info_window, null)
                    val txtPlace = view.findViewById<TextView>(R.id.txt_place)
                    val txtArrivalTime = view.findViewById<TextView>(R.id.txt_arrival_time)

                    txtPlace.text = "출발 | ${mDepartureData.placeName}"
                    txtArrivalTime.text = "-"

                    return view
                }
            }

            mDepartureInfoWindow.open(mDepartureMarker)

            if (mDestinationData.placeName != "null") {
                drawStartPlaceToDestination(it)
            }
        }
    }

    // 도착지 설정 & Map Marker 표시
    private fun setDestination() {
        binding.viewDestination.setOnClickListener {
            startForSetDestination.launch(Intent(this, SetDestinationActivity::class.java))
        }
    }

    private val startForSetDestination: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val myData =
                    result.data!!.getSerializableExtra("SearchPlaceData") as Documents
                binding.txtSelectDestination.text = "도착지 | ${myData.placeName}"
                mDestinationData = myData

                setDestinationMarker()
            }
        }

    private fun setDestinationMarker() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_naver_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fragment_naver_map, it).commit()
            }
        mapFragment.getMapAsync {
            val destinationPosition = LatLng(mDestinationData.latitude, mDestinationData.longitude)

            val cameraUpdate = CameraUpdate.scrollTo(destinationPosition)
            it.moveCamera(cameraUpdate)

            mDestinationMarker.position = destinationPosition
            mDestinationMarker.map = it

            // 출발지 미설정시 ( 소요시간 및 경로 미제공 )
            if (mDepartureData.placeName == "null") {
                mDestinationMarker.position = destinationPosition
                mDestinationMarker.map = it

                mDestinationInfoWindow.adapter = object : InfoWindow.DefaultViewAdapter(mContext) {
                    override fun getContentView(p0: InfoWindow): View {
                        val view =
                            LayoutInflater.from(mContext)
                                .inflate(R.layout.my_custom_info_window, null)
                        val txtPlace = view.findViewById<TextView>(R.id.txt_place)
                        val txtArrivalTime = view.findViewById<TextView>(R.id.txt_arrival_time)

                        txtPlace.text = "도착 | ${mDestinationData.placeName}"
                        txtArrivalTime.text = "-"
                        return view
                    }
                }

                mDestinationInfoWindow.open(mDestinationMarker)
            } else {
                // 출발지 설정시
                drawStartPlaceToDestination(it)
            }
        }
    }

    // 지도에 경로를 그려주는 함수 ( 출발지가 설정된 경우 )
    private fun drawStartPlaceToDestination(naverMap: NaverMap) {
        val points = ArrayList<LatLng>()
        points.add(LatLng(mDepartureData.latitude, mDepartureData.longitude))

        val odsay = ODsayService.init(mContext, getString(R.string.odsay_app_key))
        odsay.requestSearchPubTransPath(
            mDepartureData.longitude.toString(),
            mDepartureData.latitude.toString(),
            mDestinationData.longitude.toString(),
            mDestinationData.latitude.toString(),
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

                    mDestinationInfoWindow.adapter =
                        object : InfoWindow.DefaultViewAdapter(mContext) {
                            override fun getContentView(p0: InfoWindow): View {
                                val view =
                                    LayoutInflater.from(mContext)
                                        .inflate(R.layout.my_custom_info_window, null)
                                val txtPlace = view.findViewById<TextView>(R.id.txt_place)
                                val txtArrivalTime =
                                    view.findViewById<TextView>(R.id.txt_arrival_time)

                                txtPlace.text = "도착 | ${mDestinationData.placeName}"
                                txtArrivalTime.text = "${totalTime} 분 소요"

                                return view
                            }
                        }

                    mDestinationInfoWindow.open(mDestinationMarker)

                    val subPathArr = firstPathObj.getJSONArray("subPath")
                    for (i in 0 until subPathArr.length()) {
                        val subPathObj = subPathArr.getJSONObject(i)
                        if (!subPathObj.isNull("passStopList")) {
                            val passStopListObj = subPathObj.getJSONObject("passStopList")
                            val stationsArr = passStopListObj.getJSONArray("stations")
                            for (j in 0 until stationsArr.length()) {
                                val stationObj = stationsArr.getJSONObject(j)
                                points.add(
                                    LatLng(
                                        stationObj.getString("y").toDouble(),
                                        stationObj.getString("x").toDouble()
                                    )
                                )
                            }
                        }
                    }
                    points.add(LatLng(mDestinationData.latitude, mDestinationData.longitude))

                    mPath.coords = points
                    mPath.map = naverMap
                }

                override fun onError(p0: Int, p1: String?, p2: API?) {}
            }
        )
    }

//    // 서버에 전달할 친구id 목록 string 가공
//    fun setFriendListString(): String {
//        val sb = StringBuilder()
//
//        for (i in mSelectedFriendList) {
//            sb.append(i.id).append(",")
//        }
//        sb.deleteAt(sb.lastIndex)
//
//        return sb.toString()
//    }
//
//    // 저장 버튼 클릭 이벤트 -> 일정 저장 API 호출
//    fun saveButtonClickEvent() {
//
//        binding.btnSave.setOnClickListener {
//            val inputTitle = binding.edtAppointmentTitle.text.toString()
//            val inputPlace = binding.edtPlace.text.toString()
//            val inputDate = SimpleDateFormat("yyyy-MM-dd HH:mm").format(mSelectedDateTime.time)
//
//            val myTimeZone = mSelectedDateTime.timeZone
//            val myTimeOffset = myTimeZone.rawOffset / 1000 / 60 / 60
//            mSelectedDateTime.add(Calendar.HOUR_OF_DAY, -myTimeOffset)
//
//            if (inputTitle == "") {
//                Toast.makeText(mContext, "제목을 작성해주세요", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            if (inputDate == "") {
//                Toast.makeText(mContext, "날짜를 선택해주세요", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            if (mSelectedLat == 0.0 && mSelectedLng == 0.0) {
//                Toast.makeText(mContext, "장소을 선택해주세요", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            if (inputPlace == "") {
//                Toast.makeText(mContext, "장소를 입력해주세요", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            apiService.postRequestAddAppointment(
//                inputTitle,
//                inputDate,
//                mSelectedStartPlace.name,
//                mSelectedStartPlace.latitude,
//                mSelectedStartPlace.longitude,
//                inputPlace,
//                mSelectedLat,
//                mSelectedLng,
//                setFriendListString()
//            ).enqueue(object : Callback<BasicResponse> {
//                override fun onResponse(
//                    call: Call<BasicResponse>,
//                    response: Response<BasicResponse>
//                ) {
//                    val js = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
//                    val serviceComponent = ComponentName(mContext, MyJobService::class.java)
//
//                    mSelectedDateTime.add(Calendar.HOUR_OF_DAY, -2)
//
//                    val now = Calendar.getInstance()
//                    val timeOffset = now.timeZone.rawOffset / 1000 / 60 / 60
//                    now.add(Calendar.HOUR_OF_DAY, -timeOffset)
//
//                    val jobTime = mSelectedDateTime.timeInMillis - now.timeInMillis
//
//                    val basicResponse = response.body()!!
//
//                    val jobInfo =
//                        JobInfo.Builder(basicResponse.data.appointment.id, serviceComponent)
//                            .setMinimumLatency(jobTime)
//                            //.setMinimumLatency(TimeUnit.SECONDS.toMillis(20))
//                            .setOverrideDeadline(TimeUnit.MINUTES.toMillis(3))
//                            .build()
//
//                    js.schedule(jobInfo)
//                    finish()
//                }
//
//                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
//            })
//        }
//    }
}