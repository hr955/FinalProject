package com.example.finalproject.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.databinding.ItemFriendRequestListBinding
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.datas.UserData
import com.example.finalproject.web.ServerAPI
import com.example.finalproject.web.ServerAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 나에게 친구요청을 보낸 유저 목록
class RequestedFriendListAdapter(val mContext: Context, val mList: List<UserData>) :
    RecyclerView.Adapter<RequestedFriendListAdapter.RequestedFriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestedFriendViewHolder =
        RequestedFriendViewHolder(
            DataBindingUtil.inflate(
                parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                R.layout.item_friend_request_list,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RequestedFriendViewHolder, position: Int) {
        holder.onBind(mContext, mList[position])
    }

    override fun getItemCount(): Int = mList.size

    class RequestedFriendViewHolder(private val binding: ItemFriendRequestListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(context: Context, item: UserData) {

            binding.txtFriendNickname.text = item.nickname
            Glide.with(context).load(item.profileImgURL).into(binding.ivFriendProfile)

            binding.ivProvider.apply {
                when (item.provider) {
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

            val retrofit = ServerAPI.getRetrofit(context)
            val apiService = retrofit.create(ServerAPIService::class.java)

            val setOkorNoToServer = object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    var tag = p0!!.tag.toString()

                    apiService.putRequestFriendRequestResponse(item.id, "${tag}").enqueue(object :
                        Callback<BasicResponse> {
                        override fun onResponse(
                            call: Call<BasicResponse>,
                            response: Response<BasicResponse>
                        ) {

                        }

                        override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                        }
                    })
                }
            }

            binding.btnAcceptFriend.setOnClickListener(setOkorNoToServer)
            binding.btnDenialFriend.setOnClickListener(setOkorNoToServer)
        }
    }
}