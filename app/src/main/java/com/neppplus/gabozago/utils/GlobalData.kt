package com.neppplus.gabozago.utils

import android.content.Context
import com.neppplus.gabozago.datas.UserData

class GlobalData {

    companion object{
        var context: Context? = null
        var loginUser: UserData? = null
        set(value){
            value?.let {

                ContextUtil.setMyReadyMinute(context!!, it.readyMinute)

            }
            if(value == null){
                ContextUtil.setMyReadyMinute(context!!,0)
            }

            field = value
        }
    }

}