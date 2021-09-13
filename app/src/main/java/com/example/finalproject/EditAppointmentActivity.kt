package com.example.finalproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivityEditAppointmentBinding
import java.util.*

class EditAppointmentActivity : BaseActivity() {

    lateinit var binding:ActivityEditAppointmentBinding
    val mSelectedDateTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appointment)

        setupEvents()
        setValues()

    }

    override fun setupEvents() {
        var dateString = ""
        var timeString = ""

        // 날짜 선택 버튼
        binding.btnSelectDate.setOnClickListener {
            val dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
                dateString = "${year}년 ${month}월 ${dayOfMonth}일"

                val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
                    timeString = "${hourOfDay}시 ${minute}분"
                    binding.txtDate.text = "${dateString} ${timeString}"
                }
                TimePickerDialog(this, timeSetListener, mSelectedDateTime.get(Calendar.HOUR_OF_DAY), mSelectedDateTime.get(Calendar.MINUTE),true).show()
            }

            DatePickerDialog(this, dateSetListener, mSelectedDateTime.get(Calendar.YEAR),mSelectedDateTime.get(Calendar.MONTH),mSelectedDateTime.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 완료 버튼 (일정 저장)
        binding.btnSave.setOnClickListener {
            val inputTitle = binding.edtAppointmentTitle.text.toString()
            val inputPlace = binding.edtPlace.text.toString()

        }
    }

    override fun setValues() {
    }
}