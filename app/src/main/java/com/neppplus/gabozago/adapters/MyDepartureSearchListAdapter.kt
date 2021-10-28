package com.neppplus.gabozago.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.neppplus.gabozago.EditMyPlaceActivity
import com.neppplus.gabozago.R
import com.neppplus.gabozago.databinding.ItemSetPlaceSearchListBinding
import com.neppplus.gabozago.datas.Documents
import com.neppplus.gabozago.datas.SearchPlaceData

class MyDepartureSearchListAdapter(
    private val mContext: Context,
    private val mSearchPlaceData: SearchPlaceData
) :
    RecyclerView.Adapter<MyDepartureSearchListAdapter.ViewHolder>() {

    private var selectCheck: ArrayList<Int> = arrayListOf()

    init {
        for(i in mSearchPlaceData.documents){
            selectCheck.add(0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        DataBindingUtil.inflate(
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            R.layout.item_set_place_search_list,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(mSearchPlaceData.documents[position])
        holder.itemView.setOnClickListener {
            val test = mContext as EditMyPlaceActivity
            test.mSelectedLat = mSearchPlaceData.documents[position].latitude
            test.mSelectedLng = mSearchPlaceData.documents[position].longitude
        }
    }

    override fun getItemCount(): Int = mSearchPlaceData.documents.size

    class ViewHolder(val binding: ItemSetPlaceSearchListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Documents) {
            binding.txtPlaceName.text = item.placeName
            binding.txtPlaceAddress.text = item.addressName
        }
    }
}