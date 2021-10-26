package com.neppplus.gabozago.adapters

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
import com.neppplus.gabozago.R
import com.neppplus.gabozago.databinding.ItemFriendListBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.datas.UserData
import com.neppplus.gabozago.web.ServerAPI
import com.neppplus.gabozago.web.ServerAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 내 친구 목록
class FriendListAdapter(val mContext: Context, val mList: ArrayList<UserData>) :
    RecyclerView.Adapter<FriendListAdapter.FriendsListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsListViewHolder =
        FriendsListViewHolder(
            DataBindingUtil.inflate(
                parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                R.layout.item_friend_list,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: FriendsListViewHolder, position: Int) {
        holder.onBind(mContext, mList[position])
        holder.itemView.setOnLongClickListener {
            val alert = AlertDialog.Builder(mContext)
            alert.setTitle("친구 삭제")
            alert.setMessage("'${mList[position].nickname}'님을 친구 목록에서 삭제하시겠습니까?")
            alert.setPositiveButton("삭제", DialogInterface.OnClickListener { dialogInterface, i ->
                val retrofit = ServerAPI.getRetrofit(mContext)
                val apiService = retrofit.create(ServerAPIService::class.java)

                apiService.deleteRequestFriend(mList[position].id).enqueue(object: Callback<BasicResponse>{
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if(response.isSuccessful){
                            Toast.makeText(mContext, "친구 목록에서 삭제되었습니다", Toast.LENGTH_SHORT).show()
                            mList.removeAt(holder.bindingAdapterPosition)
                            notifyItemRemoved(holder.bindingAdapterPosition)
                            notifyItemRangeChanged(holder.bindingAdapterPosition, mList.size)
                        }
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
                })
            })
            alert.setNegativeButton("취소", null)
            alert.show()

            true
        }
    }

    override fun getItemCount(): Int = mList.size

    class FriendsListViewHolder(private val binding: ItemFriendListBinding) :
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
        }
    }
}