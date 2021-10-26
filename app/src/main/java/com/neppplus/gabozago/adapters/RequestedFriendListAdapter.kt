package com.neppplus.gabozago.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neppplus.gabozago.R
import com.neppplus.gabozago.databinding.ItemFriendRequestListBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.datas.UserData
import com.neppplus.gabozago.web.ServerAPI
import com.neppplus.gabozago.web.ServerAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

// 나에게 친구요청을 보낸 유저 목록
class RequestedFriendListAdapter(val mContext: Context, val mList: ArrayList<UserData>) :
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

    inner class RequestedFriendViewHolder(private val binding: ItemFriendRequestListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(context: Context, item: UserData) {

            binding.txtFriendNickname.text = item.nickname
            Glide.with(context).load(item.profileImgURL).into(binding.ivFriendProfile)

            binding.ivProvider.apply {
                visibility = when (item.provider) {
                    "facebook" -> {
                        setImageResource(R.drawable.ic_facebook_logo_color)
                        View.VISIBLE
                    }
                    "kakao" -> {
                        setImageResource(R.drawable.ic_kakao_logo)
                        View.VISIBLE
                    }
                    else -> View.GONE
                }
            }

            val retrofit = ServerAPI.getRetrofit(context)
            val apiService = retrofit.create(ServerAPIService::class.java)

            val setOkOrNoToServer = View.OnClickListener { p0 ->
                val tag = p0!!.tag.toString()

                apiService.putRequestFriendRequestResponse(item.id, tag).enqueue(object :
                    Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) { }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) { }
                })

                mList.remove(item)
                notifyItemRemoved(bindingAdapterPosition)
                notifyItemRangeChanged(bindingAdapterPosition, mList.size)
            }

            binding.btnAcceptFriend.setOnClickListener(setOkOrNoToServer)
            binding.btnDenialFriend.setOnClickListener(setOkOrNoToServer)
        }
    }
}