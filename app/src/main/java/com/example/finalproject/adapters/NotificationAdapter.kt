package com.example.finalproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentNotificationBinding
import com.example.finalproject.databinding.ItemNotificationBinding
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.datas.NotificationData

class NotificationAdapter(val mContext: Context, val mList: List<NotificationData>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationAdapterViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapterViewHolder = NotificationAdapterViewHolder(
        DataBindingUtil.inflate(
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            R.layout.item_notification,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: NotificationAdapterViewHolder, position: Int) {
        holder.onBind(mList[position])
        if (position == mList.size - 1) holder.viewDivisionLine.visibility = View.INVISIBLE
    }

    override fun getItemCount(): Int = mList.size

    class NotificationAdapterViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val viewDivisionLine = binding.viewDivisionLine

        fun onBind(item: NotificationData) {
            binding.txtNotificationMessage.text = item.message
            binding.txtNotificationTitle.text = item.title
            binding.txtNotificationUserDate.text = "${item.actUser.nickname} · ${item.createdAt}"

            when (item.type) {
                "약속초대" -> binding.icInviteAppointment.visibility = View.VISIBLE
                "친구추가요청" -> binding.icRequestFriend.visibility = View.VISIBLE
            }
        }
    }
}