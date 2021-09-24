package com.example.finalproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import com.example.finalproject.adapters.AddFriendSpinnerAdapter
import com.example.finalproject.adapters.StartPlaceAdapter
import com.example.finalproject.databinding.ActivityEditAppointmentBinding
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.datas.PlaceData
import com.example.finalproject.datas.UserData
import com.example.finalproject.services.MyJobService
import com.example.finalproject.utils.SizeUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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
        binding.btnSearchPlace.setOnClickListener {
            val inputPlaceName = binding.edtPlace.text.toString()

            if(inputPlaceName.length < 2){
                Toast.makeText(mContext, "검색어는 2자 이상 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url = HttpUrl.parse("https://dapi.kakao.com/v2/local/search/keyword.json")!!.newBuilder()
            url.addQueryParameter("query", inputPlaceName)

            val urlString = url.toString()

            val request = Request.Builder()
                .get()
                .header("Authorization",getString(R.string.kakao_rest_api_key))
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object: okhttp3.Callback{
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                }
            })
        }



        binding.btnAddFriendToList.setOnClickListener {

            val selectedFriend = mFriendList[binding.spinnerMyFriends.selectedItemPosition]

            if (mSelectedFriendList.contains(selectedFriend)) {
                Toast.makeText(mContext, "이미 추가된 친구입니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val textView = TextView(mContext)
            textView.setBackgroundResource(R.drawable.login_button_background)
            textView.text = selectedFriend.nickname
            textView.setPadding(SizeUtil.dpToPx(mContext, 5f).toInt())

//            val inflater = layoutInflater
//            val row = inflater.inflate(R.layout.item_add_friend_list, null)
//            val txtFriendNickname = row.findViewById<TextView>(R.id.txt_friend_nickname)
//            val btnDelete = row.findViewById<ImageView>(R.id.btn_delete)

            mSelectedFriendList.add(selectedFriend)
            textView.text = selectedFriend.nickname
            binding.layoutFriendList.addView(textView)

            textView.setOnClickListener {
                binding.layoutFriendList.removeView(textView)
                mSelectedFriendList.remove(selectedFriend)
            }
        }

        // 지도영역이 터치되면 스크롤뷰 정지
        binding.txtScrollHelp.setOnTouchListener { view, motionEvent ->
            binding.scrollView.requestDisallowInterceptTouchEvent(true)
            return@setOnTouchListener false
        }

        binding.spinnerStartPlace.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    mSelectedStartPlace = mStartPlaceList[position]

                    mNaverMap?.let {
                        drawStartPlaceToDestination(it)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

        dateSelectButtonClickEvent()
        saveButtonClickEvent()
    }

    override fun setValues() {
        txtTitle.text = "일정 추가"

        setNaverMap()
        getMyPlaceListFromServer()
        getMyFriendListFromServer()

        mStartPlaceSpinnerAdapter =
            StartPlaceAdapter(mContext, R.layout.item_my_place_list, mStartPlaceList)
        binding.spinnerStartPlace.adapter = mStartPlaceSpinnerAdapter

        mAddFriendSpinnerAdapter =
            AddFriendSpinnerAdapter(mContext, R.layout.item_friend_list, mFriendList)
        binding.spinnerMyFriends.adapter = mAddFriendSpinnerAdapter

    }

    // 출발지 목록 불러오기
    fun getMyPlaceListFromServer() {
        apiService.getRequestMyPlaceList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                mStartPlaceList.clear()
                mStartPlaceList.addAll(response.body()!!.data.places)

                mStartPlaceSpinnerAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    // 친구 목록 불러오기
    fun getMyFriendListFromServer() {
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

    // 네이버 지도 표시하기
    fun setNaverMap() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_naver_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fragment_naver_map, it).commit()
            }
        mapFragment.getMapAsync {
            mNaverMap = it

            val coord = LatLng(37.497846, 127.027357)

            val cameraUpdate = CameraUpdate.scrollTo(coord)
            it.moveCamera(cameraUpdate)

            val uiSettings = it.uiSettings
            uiSettings.isCompassEnabled = true

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
                drawStartPlaceToDestination(it)
            }
        }
    }

    // 지도에 경로를 그려주는 함수
    fun drawStartPlaceToDestination(naverMap: NaverMap) {
        mStartPlaceMarker.position =
            LatLng(mSelectedStartPlace.latitude, mSelectedStartPlace.longitude)
        mStartPlaceMarker.map = naverMap
        val points = ArrayList<LatLng>()
        points.add(LatLng(mSelectedStartPlace.latitude, mSelectedStartPlace.longitude))

        val odsay = ODsayService.init(mContext, getString(R.string.odsay_app_key))
        odsay.requestSearchPubTransPath(
            mSelectedStartPlace.longitude.toString(),
            mSelectedStartPlace.latitude.toString(),
            mSelectedLng.toString(),
            mSelectedLat.toString(),
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
                    mInfoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext) {
                        override fun getText(p0: InfoWindow): CharSequence {
                            return "${totalTime}분 소요예정"
                        }
                    }

                    mInfoWindow.open(selectedPointMarker)

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
                    points.add(LatLng(mSelectedLat, mSelectedLng))

                    mPath.coords = points
                    mPath.map = naverMap
                }

                override fun onError(p0: Int, p1: String?, p2: API?) {}
            }
        )
    }

    // 날짜 선택 버튼 클릭 이벤트
    fun dateSelectButtonClickEvent() {
        binding.btnSelectDate.setOnClickListener {
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                    mSelectedDateTime.set(year, month, day)

                    val timeSetListener =
                        TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
                            mSelectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            mSelectedDateTime.set(Calendar.MINUTE, minute)

                            binding.txtDate.text =
                                SimpleDateFormat("yyyy. M. d (E) HH:mm").format(mSelectedDateTime.time)
                        }
                    TimePickerDialog(
                        this,
                        timeSetListener,
                        mSelectedDateTime.get(Calendar.HOUR_OF_DAY),
                        mSelectedDateTime.get(Calendar.MINUTE),
                        true
                    ).show()
                }
            DatePickerDialog(
                this,
                dateSetListener,
                mSelectedDateTime.get(Calendar.YEAR),
                mSelectedDateTime.get(Calendar.MONTH),
                mSelectedDateTime.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    // 서버에 전달할 친구id 목록 string 가공
    fun setFriendListString(): String {
        val sb = StringBuilder()

        for (i in mSelectedFriendList) {
            sb.append(i.id).append(",")
        }
        sb.deleteAt(sb.lastIndex)

        return sb.toString()
    }

    // 저장 버튼 클릭 이벤트 -> 일정 저장 API 호출
    fun saveButtonClickEvent() {

        binding.btnSave.setOnClickListener {
            val inputTitle = binding.edtAppointmentTitle.text.toString()
            val inputPlace = binding.edtPlace.text.toString()
            val inputDate = SimpleDateFormat("yyyy-MM-dd HH:mm").format(mSelectedDateTime.time)

            val myTimeZone = mSelectedDateTime.timeZone
            val myTimeOffset = myTimeZone.rawOffset / 1000 / 60 / 60
            mSelectedDateTime.add(Calendar.HOUR_OF_DAY, -myTimeOffset)

            if (inputTitle == "") {
                Toast.makeText(mContext, "제목을 작성해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (inputDate == "") {
                Toast.makeText(mContext, "날짜를 선택해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (mSelectedLat == 0.0 && mSelectedLng == 0.0) {
                Toast.makeText(mContext, "장소을 선택해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (inputPlace == "") {
                Toast.makeText(mContext, "장소를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            apiService.postRequestAddAppointment(
                inputTitle,
                inputDate,
                mSelectedStartPlace.name,
                mSelectedStartPlace.latitude,
                mSelectedStartPlace.longitude,
                inputPlace,
                mSelectedLat,
                mSelectedLng,
                setFriendListString()
            ).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    val js = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
                    val serviceComponent = ComponentName(mContext, MyJobService::class.java)

                    mSelectedDateTime.add(Calendar.HOUR_OF_DAY, -2)

                    val now = Calendar.getInstance()
                    val timeOffset = now.timeZone.rawOffset / 1000 / 60 / 60
                    now.add(Calendar.HOUR_OF_DAY, -timeOffset)

                    val jobTime = mSelectedDateTime.timeInMillis - now.timeInMillis

                    val basicResponse = response.body()!!

                    val jobInfo =
                        JobInfo.Builder(basicResponse.data.appointment.id, serviceComponent)
                            .setMinimumLatency(jobTime)
                            //.setMinimumLatency(TimeUnit.SECONDS.toMillis(20))
                            .setOverrideDeadline(TimeUnit.MINUTES.toMillis(3))
                            .build()

                    js.schedule(jobInfo)
                    finish()
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
            })
        }
    }
}