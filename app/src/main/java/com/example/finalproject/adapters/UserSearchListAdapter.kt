package com.example.finalproject.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.AddFriendActivity
import com.example.finalproject.BaseActivity
import com.example.finalproject.R
import com.example.finalproject.databinding.ItemSearchFriendListBinding
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.datas.UserData
import com.example.finalproject.web.ServerAPI
import com.example.finalproject.web.ServerAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserSearchListAdapter(val mContext: Context, val mList: List<UserData>) :
    RecyclerView.Adapter<UserSearchListAdapter.UserSearchListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSearchListViewHolder =
        UserSearchListViewHolder(
            DataBindingUtil.inflate(
                parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                R.layout.item_search_friend_list,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: UserSearchListViewHolder, position: Int) {
        holder.onBind(mContext, mList[position])
    }

    override fun getItemCount(): Int = mList.size

    class UserSearchListViewHolder(private val binding: ItemSearchFriendListBinding) :
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

            binding.btnRequestFriend.setOnClickListener {
                val alert = AlertDialog.Builder(context)
                alert.setMessage("${item.nickname}님에게 친구 요청을 보내시겠습니까?")
                alert.setPositiveButton(
                    "확인",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        val retrofit = ServerAPI.getRetrofit(context)
                        val apiService = retrofit.create(ServerAPIService::class.java)

                        apiService.postRequestFriend(item.id).enqueue(object :
                            Callback<BasicResponse> {
                            override fun onResponse(
                                call: Call<BasicResponse>,
                                response: Response<BasicResponse>
                            ) {
                                Log.d("테스트",response.message())
                                if(response.isSuccessful){
                                    Log.d("테스트","성공${response.message()}")
                                    Toast.makeText(context, "${item.nickname}님에게 친구 요청을 보냈습니다", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                                TODO("Not yet implemented")
                            }

                        })
                    })
                alert.setNegativeButton("취소", null)
                alert.show()
            }
        }
    }
}