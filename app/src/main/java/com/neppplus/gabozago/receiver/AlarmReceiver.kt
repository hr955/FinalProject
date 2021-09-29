package com.neppplus.gabozago.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.neppplus.gabozago.MainActivity
import com.neppplus.gabozago.R

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        val ALARM_ID = 1001
        val PRIMARY_CHANNEL_ID = "PRIMARY_CHANNEL_ID"
    }

    lateinit var mNotificationManager: NotificationManager

    override fun onReceive(p0: Context, p1: Intent?) {
        Log.d("알람울림", "테스트로그")
        mNotificationManager =
            p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()
        deliverNotification(p0)
    }

    fun deliverNotification(context: Context) {
        val contentIntent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            ALARM_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notiBuilder = NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("테스트 알람")
            .setContentText("알림이 울립니다.")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        mNotificationManager.notify(ALARM_ID, notiBuilder.build())
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notiChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Primary Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notiChannel.enableLights(true)
            notiChannel.lightColor = Color.RED
            notiChannel.enableVibration(true)
            notiChannel.description = "알람을 통한 Notification 테스트"
            mNotificationManager.createNotificationChannel(notiChannel)
        }
    }
}