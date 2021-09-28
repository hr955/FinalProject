package com.example.finalproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.databinding.ItemAppointmentListBinding
import com.example.finalproject.datas.AppointmentData

class InvitedAppointmentListAdapter(val mContext: Context, val mList: List<AppointmentData> ) : RecyclerView.Adapter<InvitedAppointmentListAdapter.InvitedAppointmentListAdapterViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InvitedAppointmentListAdapterViewHolder = InvitedAppointmentListAdapterViewHolder(
        DataBindingUtil.inflate(
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            R.layout.item_appointment_list,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: InvitedAppointmentListAdapterViewHolder, position: Int) {
        holder.onBind(mList[position])
    }

    override fun getItemCount(): Int = mList.size
    class InvitedAppointmentListAdapterViewHolder(private val binding: ItemAppointmentListBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun onBind(item: AppointmentData){
                binding.txtTitle.text = item.title
                binding.txtDate.text = item.getFormattedDateTime()
                binding.txtPlace.text = item.place
            }
    }
}