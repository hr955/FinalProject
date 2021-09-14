package com.example.finalproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivityEditAppointmentBinding
import com.example.finalproject.datas.BasicResponse
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditAppointmentActivity : BaseActivity() {

    lateinit var binding: ActivityEditAppointmentBinding
    val mSelectedDateTime = Calendar.getInstance()

    var mSelectedLat = 0.0
    var mSelectedLng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appointment)

        setupEvents()
        setValues()

    }

    override fun setupEvents() {

        dateSelectButtonClickEvent()
        saveButtonClickEvent()
    }

    override fun setValues() {
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

    // 저장 버튼 클릭 이벤트 -> 일정 저장 API 호출
    fun saveButtonClickEvent() {
        binding.btnSave.setOnClickListener {
            val inputTitle = binding.edtAppointmentTitle.text.toString()
            val inputPlace = binding.edtPlace.text.toString()
            val inputDate = SimpleDateFormat("yyyy-MM-dd HH:mm").format(mSelectedDateTime.time)

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

            apiService.postRequestAddAppointment(
                inputTitle,
                inputDate,
                inputPlace,
                mSelectedLat,
                mSelectedLng
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


}