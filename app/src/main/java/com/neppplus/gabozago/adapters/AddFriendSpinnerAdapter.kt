package com.neppplus.gabozago.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.neppplus.gabozago.R
import com.neppplus.gabozago.datas.UserData

class AddFriendSpinnerAdapter(
    val mContext: Context,
    resId: Int,
    val mList: List<UserData>
) : ArrayAdapter<UserData>(mContext, resId, mList) {

    val mInflater = LayoutInflater.from(mContext)


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView

        if (row == null) {
            row = mInflater.inflate(R.layout.item_friend_list, null)
        }
        row!!

        val data = mList[position]

        val txtFriendNickname = row.findViewById<TextView>(R.id.txt_friend_nickname)
        val ivProvider = row.findViewById<ImageView>(R.id.iv_provider)

        txtFriendNickname.text = data.nickname

        ivProvider.apply {
            when (data.provider) {
                "facebook" -> {
                    setImageResource(R.drawable.ic_facebook_logo_color)
                    visibility = View.VISIBLE
                }
                "kakao" -> {
                    setImageResource(R.drawable.ic_kakao_logo)
                    visibility = View.VISIBLE
                }
                else -> visibility = View.GONE
            }
        }

        return row
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView

        if (row == null) {
            row = mInflater.inflate(R.layout.item_friend_list, null)
        }
        row!!

        val data = mList[position]

        val txtFriendNickname = row.findViewById<TextView>(R.id.txt_friend_nickname)
        val ivProvider = row.findViewById<ImageView>(R.id.iv_provider)

        ivProvider.apply {
            when (data.provider) {
                "facebook" -> {
                    setImageResource(R.drawable.ic_facebook_logo_color)
                    visibility = View.VISIBLE
                }
                "kakao" -> {
                    setImageResource(R.drawable.ic_kakao_logo)
                    visibility = View.VISIBLE
                }
                else -> visibility = View.GONE
            }
        }

        txtFriendNickname.text = data.nickname

        return row
    }
}