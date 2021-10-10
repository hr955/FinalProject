package com.neppplus.gabozago.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.neppplus.gabozago.R
import com.neppplus.gabozago.SetDepartureActivity
import com.neppplus.gabozago.databinding.ItemSetDepartureMyPlaceBinding
import com.neppplus.gabozago.datas.PlaceData

class SetDepartureMyPlaceListAdapter(val mContext: Context, val mList: List<PlaceData>) :
    RecyclerView.Adapter<SetDepartureMyPlaceListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        DataBindingUtil.inflate(
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            R.layout.item_set_departure_my_place,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(mList[position])

        holder.itemView.setOnClickListener {
            val activity = mContext as SetDepartureActivity
            activity.intent.putExtra("PlaceData", mList[position])
            activity.setResult(Activity.RESULT_OK, activity.intent)
            activity.finish()
        }
    }

    override fun getItemCount(): Int = mList.size

    class ViewHolder(private val binding: ItemSetDepartureMyPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: PlaceData) {
            binding.txtMyPlaceName.text = item.name
        }
    }
}