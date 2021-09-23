package com.example.finalproject.utils

import android.content.Context

class ContextUtil {

    companion object{
        private val prefName = "AppointmentPref"
        private val TOKEN = "token"
        val MY_READY_MINUTE = "MY_READY_MINUTE"

        // 토큰 저장 함수
        fun setToken(context: Context, token: String) {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            pref.edit().putString(TOKEN, token).apply()
        }

        // 저장된 토큰 읽어오는 함수
        fun getToken(context: Context): String {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            return pref.getString(TOKEN, "")!!
        }

        // 토큰 저장 함수
        fun setMyReadyMinute(context: Context, minute: Int) {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            pref.edit().putInt(MY_READY_MINUTE, minute).apply()
        }

        // 저장된 토큰 읽어오는 함수
        fun getMyReadyMinute(context: Context): Int {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            return pref.getInt(MY_READY_MINUTE, 0)!!
        }
    }
}