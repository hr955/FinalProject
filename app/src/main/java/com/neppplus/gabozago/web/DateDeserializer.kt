package com.neppplus.gabozago.web

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class DateDeserializer : JsonDeserializer<Date> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date {
        val dateStr = json?.asString
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        // 서버가 준 String -> Date 형식으로 변환 | 서버 기본값 : UTC
        val resultDate = sdf.parse(dateStr)
        // UTC -> 디바이스 시간으로 보정
        val now = Calendar.getInstance()

        // 결과로 나갈 시간값의 밀리초단위
        resultDate.time += now.timeZone.rawOffset

        // 시차만큼 더해진 시간값 파싱 결과
        return resultDate
    }
}