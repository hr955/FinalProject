package com.neppplus.gabozago.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.neppplus.gabozago.R
import com.neppplus.gabozago.ViewAppointmentDetailActivity
import com.neppplus.gabozago.ViewMapActivity
import com.neppplus.gabozago.databinding.ItemAppointmentListBinding
import com.neppplus.gabozago.datas.AppointmentData

class AppointmentListAdapter(val mContext: Context, private val mList: List<AppointmentData>) :
    RecyclerView.Adapter<AppointmentListAdapter.AppointmentListAdapterViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentListAdapterViewHolder = AppointmentListAdapterViewHolder(
        DataBindingUtil.inflate(
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            R.layout.item_appointment_list,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: AppointmentListAdapterViewHolder, position: Int) {
        holder.onBind(mList[position])
        holder.onMapClickEvent(mList[position], mContext)
    }

    override fun getItemCount(): Int = mList.size

    class AppointmentListAdapterViewHolder(private val binding: ItemAppointmentListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: AppointmentData) {
            binding.txtTitle.text = item.title
            Log.d("appointmentDate", item.datetime.toString())
            binding.txtDate.text = item.getFormattedDateTime()
            binding.txtPlace.text = item.place
        }

        fun onMapClickEvent(item: AppointmentData, context: Context) {
            binding.btnMapDetail.setOnClickListener {
                val myIntent = Intent(context, ViewMapActivity::class.java)
                myIntent.putExtra("AppointmentData", item)
                context.startActivity(myIntent)
            }

            binding.layoutRoot.setOnClickListener {
                val myIntent = Intent(context, ViewAppointmentDetailActivity::class.java)
                myIntent.putExtra("AppointmentData", item)
                context.startActivity(myIntent)
            }
        }
    }
}