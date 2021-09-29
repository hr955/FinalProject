package com.neppplus.gabozago.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import com.neppplus.gabozago.R
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.receiver.AlarmReceiver
import com.neppplus.gabozago.utils.ContextUtil
import com.neppplus.gabozago.web.ServerAPI
import com.neppplus.gabozago.web.ServerAPIService
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MyJobService : JobService() {

    companion object {
        val JOB_TIME_SET = 1000
    }

    override fun onStartJob(p0: JobParameters?): Boolean {

        val retrofit = ServerAPI.getRetrofit(applicationContext)
        val apiService = retrofit.create(ServerAPIService::class.java)

        apiService.getRequestAppointmentDetail(p0!!.jobId)
            .enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.isSuccessful) {
                        val basicResponse = response.body()!!
                        val appointmentData = basicResponse.data.appointment

                        val odsay =
                            ODsayService.init(applicationContext, getString(R.string.odsay_app_key))
                        odsay.requestSearchPubTransPath(
                            appointmentData.startlongitude.toString(),
                            appointmentData.startlatitude.toString(),
                            appointmentData.longitude.toString(),
                            appointmentData.latitude.toString(),
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

                                    val now = Calendar.getInstance()
                                    appointmentData.datetime.time += now.timeZone.rawOffset
                                    val alarmTime =
                                        appointmentData.datetime.time - totalTime * 60 * 1000 - ContextUtil.getMyReadyMinute(applicationContext) * 60 * 1000

                                    setAlarmByMillisecond(alarmTime)

                                }

                                override fun onError(p0: Int, p1: String?, p2: API?) {}
                            }
                        )
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
            })
        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {

        return false
    }

    fun setAlarmByMillisecond(timeInMillis: Long) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            AlarmReceiver.ALARM_ID,
            myIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val triggerTime = timeInMillis

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent)
    }
}