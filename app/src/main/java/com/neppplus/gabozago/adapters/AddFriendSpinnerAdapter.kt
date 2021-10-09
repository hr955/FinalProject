package com.neppplus.gabozago.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.neppplus.gabozago.R
import com.neppplus.gabozago.databinding.ItemSpinnerFriendListBinding
import com.neppplus.gabozago.datas.UserData

class AddFriendSpinnerAdapter(
    val mContext: Context,
    resId: Int,
    private val mList: List<UserData>
) : ArrayAdapter<UserData>(mContext, resId, mList) {

    lateinit var binding: ItemSpinnerFriendListBinding

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        binding = DataBindingUtil.inflate(
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            R.layout.item_spinner_friend_list,
            parent,
            false
        )
        val data = mList[position]

        binding.txtNickname.text = data.nickname
        Glide.with(mContext).load(data.profileImgURL).into(binding.ivProfileImage)

//        ivProvider.apply {
//            when (data.provider) {
//                "facebook" -> {
//                    setImageResource(R.drawable.ic_facebook_logo_color)
//                    visibility = View.VISIBLE
//                }
//                "kakao" -> {
//                    setImageResource(R.drawable.ic_kakao_logo)
//                    visibility = View.VISIBLE
//                }
//                else -> visibility = View.GONE
//            }
//        }

        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        binding = DataBindingUtil.inflate(
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            R.layout.item_spinner_friend_list,
            parent,
            false
        )

        val data = mList[position]

        binding.txtNickname.text = data.nickname
        Glide.with(mContext).load(data.profileImgURL).into(binding.ivProfileImage)
//
//        ivProvider.apply {
//            when (data.provider) {
//                "facebook" -> {
//                    setImageResource(R.drawable.ic_facebook_logo_color)
//                    visibility = View.VISIBLE
//                }
//                "kakao" -> {
//                    setImageResource(R.drawable.ic_kakao_logo)
//                    visibility = View.VISIBLE
//                }
//                else -> visibility = View.GONE
//            }
//        }

        return binding.root
    }
}