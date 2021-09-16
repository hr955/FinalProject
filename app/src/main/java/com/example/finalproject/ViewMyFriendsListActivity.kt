package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    }

    override fun setValues() {
        mAdapter = FriendsListViewPagerAdapter(supportFragmentManager)
        binding.vpFriends.adapter = mAdapter
        binding.tlFriends.setupWithViewPager(binding.vpFriends)
    }
}