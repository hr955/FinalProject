package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.finalproject.adapters.FriendsListViewPagerAdapter
import com.example.finalproject.databinding.ActivityViewMyFriendsListBinding

class ViewMyFriendsListActivity : BaseActivity() {

    lateinit var binding: ActivityViewMyFriendsListBinding
    lateinit var mAdapter: FriendsListViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_my_friends_list)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        btnAdd.setOnClickListener {
            startActivity(Intent(mContext, FindFriendActivity::class.java))
        }
    }

    override fun setValues() {
        txtTitle.text = "친구 목록 관리"
        mAdapter = FriendsListViewPagerAdapter(supportFragmentManager)
        binding.vpFriends.adapter = mAdapter
        binding.tlFriends.setupWithViewPager(binding.vpFriends)
        btnAdd.visibility = View.VISIBLE
    }
}