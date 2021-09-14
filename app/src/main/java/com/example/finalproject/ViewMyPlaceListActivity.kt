package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivityViewMyPlaceListBinding

class ViewMyPlaceListActivity : BaseActivity() {

    lateinit var binding: ActivityViewMyPlaceListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_my_place_list)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        btnAddPlace.setOnClickListener {
            startActivity(Intent(mContext, EditMyPlaceActivity::class.java))
        }
    }

    override fun setValues() {
        txtTitle.text = "출발지 목록"
        btnAddPlace.visibility = View.VISIBLE
    }
}