package com.example.finalproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivityEditAppointmentBinding
import com.example.finalproject.datas.BasicResponse
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditAppointmentActivity : BaseActivity() {

    lateinit var binding: ActivityEditAppointmentBinding
    val mSelectedDateTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appointment)

        setupEvents()
        setValues()

    }

    override fun setupEvents() {
        // 날짜 선택 버튼
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

        // 완료 버튼 (일정 저장)
        binding.btnSave.setOnClickListener {
            val inputTitle = binding.edtAppointmentTitle.text.toString()
            val inputPlace = binding.edtPlace.text.toString()
            val inputDate = SimpleDateFormat("yyyy-MM-dd HH:mm").format(mSelectedDateTime.time)
            val lat = 37.497919
            val lon = 127.027469

            apiService.postRequestAppointment(
                inputTitle,
                inputDate,
                inputPlace,
                lat,
                lon
            ).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {

                    Log.d("테스트", response.body().toString())
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Log.d("테스트", t.message.toString())

                }
            })

            if (binding.txtDate.text == "") {
                // TODO 제목/날짜/장소 미선택시 분기처리
            }
        }
    }

    override fun setValues() {
        val mapView = MapView(mContext)
        binding.mapView.addView(mapView)
    }
}