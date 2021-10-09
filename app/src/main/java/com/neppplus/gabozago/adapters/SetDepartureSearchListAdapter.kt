package com.neppplus.gabozago.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.neppplus.gabozago.R
import com.neppplus.gabozago.databinding.ItemSetDepartureSearchListBinding
import com.neppplus.gabozago.datas.SearchPlaceData

class SetDepartureSearchListAdapter(val mList: List<SearchPlaceData>) :
    RecyclerView.Adapter<SetDepartureSearchListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        DataBindingUtil.inflate(
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            R.layout.item_set_departure_search_list,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(mList[position])

        if (position == mList.size - 1) holder.viewDivisionLine.visibility = View.GONE
    }

    override fun getItemCount(): Int = mList.size

    class ViewHolder(private val binding: ItemSetDepartureSearchListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val viewDivisionLine = binding.viewDivisionLine

        fun onBind(item: SearchPlaceData) {
            binding.txtPlaceName.text = item.placeName
            binding.txtPlaceAddress.text = item.addressName

        }
    }
}