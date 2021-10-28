package com.neppplus.gabozago.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.neppplus.gabozago.R
import com.neppplus.gabozago.databinding.ItemMyPlaceListBinding
import com.neppplus.gabozago.datas.BasicResponse
import com.neppplus.gabozago.datas.PlaceData
import com.neppplus.gabozago.web.ServerAPI
import com.neppplus.gabozago.web.ServerAPIService
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

        holder.itemView.setOnLongClickListener {
            val retrofit = ServerAPI.getRetrofit(mContext)
            val apiService = retrofit.create(ServerAPIService::class.java)

            val alert = AlertDialog.Builder(mContext)
            alert.setTitle("출발지 삭제")
            alert.setMessage("'${data.name}'을(를) 내 출발지 목록에서 삭제하시겠습니까?")
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

            true
        }
    }

    override fun getItemCount(): Int = mList.size

    class MyPlaceListAdapterViewHolder(private val binding: ItemMyPlaceListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: PlaceData) {
            binding.txtMyPlaceName.text = item.name
        }
    }
}