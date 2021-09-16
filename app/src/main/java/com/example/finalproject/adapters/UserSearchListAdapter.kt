package com.example.finalproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.databinding.ItemFriendListBinding
import com.example.finalproject.datas.UserData

class UserSearchListAdapter(val mContext: Context, val mList: List<UserData>) :
    RecyclerView.Adapter<UserSearchListAdapter.UserSearchListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSearchListViewHolder =
        UserSearchListViewHolder(
            DataBindingUtil.inflate(
                parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                R.layout.item_friend_list,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: UserSearchListViewHolder, position: Int) {
        holder.onBind(mContext, mList[position])
    }

    override fun getItemCount(): Int = mList.size

    class UserSearchListViewHolder(private val binding: ItemFriendListBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun onBind(context: Context, item: UserData){
                binding.txtFriendNickname.text = item.nickname

                Glide.with(context).load(item.profileImgURL).into(binding.ivFriendProfile)

                binding.ivProvider.apply {
                    when(item.provider){
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
            }
    }
}