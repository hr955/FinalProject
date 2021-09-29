package com.neppplus.gabozago.datas

// 서버가 주는 기본형태의 응답
class BasicResponse(
    var code: Int,
    var message: String,
    var data: DataResponse
) {
}