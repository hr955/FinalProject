package com.example.finalproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.databinding.ItemMyPlaceListBinding
import com.example.finalproject.datas.PlaceData

class MyPlaceListAdapter(val mContext: Context, private val mList: List<PlaceData>) :
    RecyclerView.Adapter<MyPlaceListAdapter.MyPlaceListAdapterViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyPlaceListAdapterViewHolder = MyPlaceListAdapterViewHolder(
        DataBindingUtil.inflate(
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            R.layout.item_my_place_list,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: MyPlaceListAdapterViewHolder, position: Int) {
        holder.onBind(mList[position])
    }

    override fun getItemCount(): Int = mList.size

    class MyPlaceListAdapterViewHolder(private val binding: ItemMyPlaceListBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun onBind(item: PlaceData){
                binding.txtMyPalceName.text = item.name
                if(item.isPrimary){
                    binding.txtIsPrimary.visibility = View.VISIBLE
                }else{
                    binding.txtIsPrimary.visibility = View.GONE
                }
            }
    }
}