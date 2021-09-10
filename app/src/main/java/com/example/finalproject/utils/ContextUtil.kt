package com.example.finalproject.utils

import android.content.Context

class ContextUtil {

    companion object{
        private val tokenPref = "tokenPref"
        private val TOKEN = "token"

        // 토큰 저장 함수
        fun setToken(context: Context, token: String) {
            val pref = context.getSharedPreferences(tokenPref, Context.MODE_PRIVATE)
            pref.edit().putString(TOKEN, token).apply()
        }

        // 저장된 토큰 읽어오는 함수
        fun getToken(context: Context): String {
            val pref = context.getSharedPreferences(tokenPref, Context.MODE_PRIVATE)
            return pref.getString(TOKEN, "")!!
        }
    }
}