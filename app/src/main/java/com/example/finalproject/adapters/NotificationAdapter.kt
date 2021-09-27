package com.example.finalproject.adapters

import android.content.Context
import android.view.LayoutInflater
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
    }

    override fun getItemCount(): Int = mList.size

    class NotificationAdapterViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: NotificationData) {
            binding.txtNotificationMessage.text = item.message
        }
    }
}