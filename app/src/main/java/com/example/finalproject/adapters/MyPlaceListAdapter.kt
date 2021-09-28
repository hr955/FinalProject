package com.example.finalproject.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.databinding.ItemMyPlaceListBinding
import com.example.finalproject.datas.BasicResponse
import com.example.finalproject.datas.PlaceData
import com.example.finalproject.web.ServerAPI
import com.example.finalproject.web.ServerAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPlaceListAdapter(val mContext: Context, private val mList: ArrayList<PlaceData>) :
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
        val data = mList[position]

        holder.onBind(data)

        holder.btnDeleteMyPlace.setOnClickListener {
            val retrofit = ServerAPI.getRetrofit(mContext)
            val apiService = retrofit.create(ServerAPIService::class.java)

            val alert = AlertDialog.Builder(mContext)
            alert.setTitle("'${data.name}'을(를) 삭제하시겠습니까?")
            alert.setPositiveButton("삭제", DialogInterface.OnClickListener { dialogInterface, i ->
                apiService.deleteRequestMyPlace(data.id).enqueue(object : Callback<BasicResponse>{
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if(response.isSuccessful)Log.d("MyPlaceDelete", "My Place Delete Successful")
                        mList.remove(data)
                        notifyItemRemoved(holder.bindingAdapterPosition)
                        notifyItemRangeChanged(holder.bindingAdapterPosition, mList.size)
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {}
                })
            })
            alert.setNegativeButton("취소", null)
            alert.show()
        }
    }

    override fun getItemCount(): Int = mList.size

    class MyPlaceListAdapterViewHolder(private val binding: ItemMyPlaceListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val btnDeleteMyPlace = binding.btnDeleteMyPlace

        fun onBind(item: PlaceData) {
            binding.txtMyPlaceName.text = item.name
            if (item.isPrimary) {
                binding.txtIsPrimary.visibility = View.VISIBLE
            } else {
                binding.txtIsPrimary.visibility = View.GONE
            }
        }
    }
}