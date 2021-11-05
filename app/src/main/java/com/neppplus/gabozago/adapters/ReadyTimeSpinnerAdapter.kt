package com.neppplus.gabozago.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.neppplus.gabozago.R
import com.neppplus.gabozago.databinding.ItemSpinnerReadyTimeBinding
import com.neppplus.gabozago.datas.UserData

class ReadyTimeSpinnerAdapter(
    val mContext: Context,
    resId: Int,
    private val mList: Array<Int>
) : ArrayAdapter<Int>(mContext, resId, mList) {

    lateinit var binding: ItemSpinnerReadyTimeBinding

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (convertView != null) {
            return binding.root
        } else {
            binding = DataBindingUtil.inflate(
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                R.layout.item_spinner_ready_time,
                parent,
                false
            )
        }

        binding.txtTime.text = mList[position].toString()

        return binding.root

    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        binding = DataBindingUtil.inflate(
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            R.layout.item_spinner_ready_time,
            parent,
            false
        )

        binding.txtTime.text = mList[position].toString()

        return binding.root
    }

}