package com.neppplus.gabozago

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.neppplus.gabozago.adapters.AddFriendSpinnerAdapter
import com.neppplus.gabozago.adapters.StartPlaceAdapter
import com.neppplus.gabozago.databinding.ActivityEditAppointmentBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.datas.PlaceData
import com.neppplus.gabozago.datas.UserData
import com.neppplus.gabozago.utils.SizeUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditAppointmentActivity : BaseActivity() {

    lateinit var binding: ActivityEditAppointmentBinding
    val mSelectedDateTime = Calendar.getInstance()

    val mStartPlaceList = ArrayList<PlaceData>()
    val mFriendList = ArrayList<UserData>()
    lateinit var mStartPlaceSpinnerAdapter: StartPlaceAdapter
    lateinit var mAddFriendSpinnerAdapter: AddFriendSpinnerAdapter
    lateinit var mSelectedStartPlace: PlaceData
    val mStartPlaceMarker = Marker()
    val mPath = PathOverlay()

    lateinit var mPlaceName: String

    val mSelectedFriendList = ArrayList<UserData>()

    val selectedPointMarker = Marker()

    val mInfoWindow = InfoWindow()

    var mNaverMap: NaverMap? = null

    var mSelectedLat = 0.0
    var mSelectedLng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appointment)

        setupEvents()
        setValues()

    }

    override fun setupEvents() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        setDeparture()
        dateSelectButtonClickEvent()
        timeSelectButtonClickEvent()
        addFriendButtonClickEvent()

        // 지도영역이 터치되면 스크롤뷰 정지
        binding.txtScrollHelp.setOnTouchListener { view, motionEvent ->
            binding.scrollview.requestDisallowInterceptTouchEvent(true)
            return@setOnTouchListener false
        }


//        dateSelectButtonClickEvent()
//        saveButtonClickEvent()
    }

    override fun setValues() {
//        setNaverMap()
//        getMyPlaceListFromServer()

        getMyFriendListFromServer()

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

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
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

    // 출발지 설정
    private fun setDeparture() {
        binding.viewDeparture.setOnClickListener {
            startForSetDeparture.launch(Intent(this, SetDepartureActivity::class.java))
        }
    }

    private val startForSetDeparture: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val myData = result.data!!.getSerializableExtra("PlaceData") as PlaceData
                binding.txtSelectDeparture.text = myData?.name
            }
        }

//    fun 임시함수(){
//        binding.btnSearchPlace.setOnClickListener {
//            val inputPlaceName = binding.edtPlace.text.toString()
//
//            if(inputPlaceName.length < 2){
//                Toast.makeText(mContext, "검색어는 2자 이상 입력해주세요", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            val url = HttpUrl.parse("https://dapi.kakao.com/v2/local/search/keyword.json")!!.newBuilder()
//            url.addQueryParameter("query", inputPlaceName)
//
//            val urlString = url.toString()
//
//            val request = Request.Builder()
//                .url(urlString)
//                .get()
//                .header("Authorization",getString(R.string.kakao_rest_api_key))
//                .build()
//
//            val client = OkHttpClient()
//            client.newCall(request).enqueue(object: okhttp3.Callback{
//                override fun onFailure(call: okhttp3.Call, e: IOException) {
//                }
//
//                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
//                    val jsonObj = JSONObject(response.body()!!.string())
//
//                    val documentsArr = jsonObj.getJSONArray("documents")
//                    for(i in 0 until documentsArr.length()){
//                        val docu = documentsArr.getJSONObject(i)
//                        val placeName = docu.getString("place_name")
//                        val lat = docu.getString("y").toDouble()
//                        val lng = docu.getString("x").toDouble()
//
//                        // 첫번째 검색 결과만 파싱
//
//                        runOnUiThread {
//                            binding.edtPlace.setText(placeName)
//
//                            val findPlaceLatLng = LatLng(lat, lng)
//
//                            selectedPointMarker.position = findPlaceLatLng
//                            selectedPointMarker.map = mNaverMap
//
//                            mNaverMap?.moveCamera(CameraUpdate.scrollTo(findPlaceLatLng))
//
//                            mSelectedLat = lat
//                            mSelectedLng = lng
//
//                            drawStartPlaceToDestination(mNaverMap!!)
//                        }
//                            break
//                    }
//                }
//            })
//        }
//
//        binding.spinnerStartPlace.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    p0: AdapterView<*>?,
//                    p1: View?,
//                    position: Int,
//                    p3: Long
//                ) {
//                    mSelectedStartPlace = mStartPlaceList[position]
//
//                    mNaverMap?.let {
//                        drawStartPlaceToDestination(it)
//                    }
//                }
//
//                override fun onNothingSelected(p0: AdapterView<*>?) {}
//            }
//    }

    //    // 출발지 목록 불러오기
//    fun getMyPlaceListFromServer() {
//        apiService.getRequestMyPlaceList().enqueue(object : Callback<BasicResponse> {
//            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
//                mStartPlaceList.clear()
//                mStartPlaceList.addAll(response.body()!!.data.places)
//
//                mStartPlaceSpinnerAdapter.notifyDataSetChanged()
//            }
//
//            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//        })
//    }
//
//
//    // 네이버 지도 표시하기
//    fun setNaverMap() {
//        val fm = supportFragmentManager
//        val mapFragment = fm.findFragmentById(R.id.fragment_naver_map) as MapFragment?
//            ?: MapFragment.newInstance().also {
//                fm.beginTransaction().add(R.id.fragment_naver_map, it).commit()
//            }
//        mapFragment.getMapAsync {
//            mNaverMap = it
//
//            val coord = LatLng(37.497846, 127.027357)
//
//            val cameraUpdate = CameraUpdate.scrollTo(coord)
//            it.moveCamera(cameraUpdate)
//
//            val uiSettings = it.uiSettings
//            uiSettings.isCompassEnabled = true
//
//            selectedPointMarker.icon = OverlayImage.fromResource(R.drawable.ic_pink_marker)
//
//            it.setOnMapClickListener { point, coord ->
//                Toast.makeText(
//                    this, "${coord.latitude}, ${coord.longitude}",
//                    Toast.LENGTH_SHORT
//                ).show()
//                mSelectedLat = coord.latitude
//                mSelectedLng = coord.longitude
//
//                selectedPointMarker.position = LatLng(mSelectedLat, mSelectedLng)
//                selectedPointMarker.map = it
//                drawStartPlaceToDestination(it)
//            }
//        }
//    }
//
//    // 지도에 경로를 그려주는 함수
//    fun drawStartPlaceToDestination(naverMap: NaverMap) {
//        mStartPlaceMarker.position =
//            LatLng(mSelectedStartPlace.latitude, mSelectedStartPlace.longitude)
//        mStartPlaceMarker.map = naverMap
//        val points = ArrayList<LatLng>()
//        points.add(LatLng(mSelectedStartPlace.latitude, mSelectedStartPlace.longitude))
//
//        val odsay = ODsayService.init(mContext, getString(R.string.odsay_app_key))
//        odsay.requestSearchPubTransPath(
//            mSelectedStartPlace.longitude.toString(),
//            mSelectedStartPlace.latitude.toString(),
//            mSelectedLng.toString(),
//            mSelectedLat.toString(),
//            null,
//            null,
//            null,
//            object : OnResultCallbackListener {
//                override fun onSuccess(p0: ODsayData?, p1: API?) {
//                    val jsonObj = p0!!.json
//                    val resultObj = jsonObj.getJSONObject("result")
//                    val pathArr = resultObj.getJSONArray("path")
//                    val firstPathObj = pathArr.getJSONObject(0)
//
//                    val infoObj = firstPathObj.getJSONObject("info")
//                    val totalTime = infoObj.getInt("totalTime")
//                    mInfoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext) {
//                        override fun getText(p0: InfoWindow): CharSequence {
//                            return "${totalTime}분 소요예정"
//                        }
//                    }
//
//                    mInfoWindow.open(selectedPointMarker)
//
//                    val subPathArr = firstPathObj.getJSONArray("subPath")
//                    for (i in 0 until subPathArr.length()) {
//                        val subPathObj = subPathArr.getJSONObject(i)
//                        if (!subPathObj.isNull("passStopList")) {
//                            val passStopListObj = subPathObj.getJSONObject("passStopList")
//                            val stationsArr = passStopListObj.getJSONArray("stations")
//                            for (j in 0 until stationsArr.length()) {
//                                val stationObj = stationsArr.getJSONObject(j)
//                                points.add(
//                                    LatLng(
//                                        stationObj.getString("y").toDouble(),
//                                        stationObj.getString("x").toDouble()
//                                    )
//                                )
//                            }
//                        }
//                    }
//                    points.add(LatLng(mSelectedLat, mSelectedLng))
//
//                    mPath.coords = points
//                    mPath.map = naverMap
//                }
//
//                override fun onError(p0: Int, p1: String?, p2: API?) {}
//            }
//        )
//    }


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