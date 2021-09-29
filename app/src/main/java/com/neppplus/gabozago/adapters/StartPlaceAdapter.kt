package com.neppplus.gabozago.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.neppplus.gabozago.R
import com.neppplus.gabozago.datas.PlaceData

class StartPlaceAdapter(
    val mContext: Context,
    resId: Int,
    val mList: List<PlaceData>
) : ArrayAdapter<PlaceData>(mContext, resId, mList) {

    val mInflater = LayoutInflater.from(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        if (row == null) {
            row = mInflater.inflate(R.layout.item_my_place_list, null)
        }
        row!!

        val data = mList[position]

        val txtMyPlaceName = row.findViewById<TextView>(R.id.txt_my_place_name)
        val txtIsPrimary = row.findViewById<TextView>(R.id.txt_is_primary)
        val ivMyPlaceMap = row.findViewById<ImageView>(R.id.iv_my_place_map)

        txtMyPlaceName.text = data.name

        if (data.isPrimary) {
            txtIsPrimary.visibility = View.VISIBLE
        } else {
            txtIsPrimary.visibility = View.GONE
        }
        ivMyPlaceMap.visibility = View.GONE

        return row
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        if (row == null) {
            row = mInflater.inflate(R.layout.item_my_place_list, null)
        }
        row!!

        val data = mList[position]

        val txtMyPlaceName = row.findViewById<TextView>(R.id.txt_my_place_name)
        val txtIsPrimary = row.findViewById<TextView>(R.id.txt_is_primary)
        val ivMyPlaceMap = row.findViewById<ImageView>(R.id.iv_my_place_map)

        txtMyPlaceName.text = data.name

        if (data.isPrimary) {
            txtIsPrimary.visibility = View.VISIBLE
        } else {
            txtIsPrimary.visibility = View.GONE
        }
        ivMyPlaceMap.visibility = View.GONE

        return row

    }
}