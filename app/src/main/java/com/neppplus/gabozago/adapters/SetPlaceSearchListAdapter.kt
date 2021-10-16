package com.neppplus.gabozago.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.neppplus.gabozago.R
import com.neppplus.gabozago.SetDepartureActivity
import com.neppplus.gabozago.SetDestinationActivity
import com.neppplus.gabozago.databinding.ItemSetPlaceSearchListBinding
import com.neppplus.gabozago.datas.Documents
import com.neppplus.gabozago.datas.SearchPlaceData

class SetPlaceSearchListAdapter(val mContext: Context, val mList: SearchPlaceData) :
    RecyclerView.Adapter<SetPlaceSearchListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        DataBindingUtil.inflate(
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            R.layout.item_set_place_search_list,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(mList.documents[position])

        holder.itemView.setOnClickListener {
            val activity = mContext as Activity
            activity.intent.putExtra("DepartureType", "SearchList")
            activity.intent.putExtra("SearchPlaceData", mList.documents[position])
            activity.setResult(Activity.RESULT_OK, activity.intent)
            activity.finish()
        }

        if (position == mList.documents.size - 1) holder.viewDivisionLine.visibility = View.GONE
    }

    override fun getItemCount(): Int = mList.documents.size

    class ViewHolder(private val binding: ItemSetPlaceSearchListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val viewDivisionLine = binding.viewDivisionLine

        fun onBind(item: Documents) {
            binding.txtPlaceName.text = item.placeName
            binding.txtPlaceAddress.text = item.addressName

        }
    }
}