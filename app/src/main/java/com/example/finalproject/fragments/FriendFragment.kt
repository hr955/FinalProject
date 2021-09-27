package com.example.finalproject.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.finalproject.R
import com.example.finalproject.adapters.FriendsListViewPagerAdapter
import com.example.finalproject.databinding.FragmentFriendBinding
import com.google.android.material.tabs.TabLayoutMediator

class FriendFragment : BaseFragment(){

    lateinit var binding: FragmentFriendBinding
    lateinit var mAdapter: FriendsListViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_friend, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

    }

    override fun setValues() {
        txtTitle.text = "친구"
        btnFindFriend.visibility = View.VISIBLE

        binding.vpFriend.adapter = FriendsListViewPagerAdapter(this)
        Log.d("테스트", binding.vpFriend.adapter.toString())
        binding.vpFriend.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(binding.tabFriend, binding.vpFriend) { tab, position ->
            when(position){
                0-> tab.text = "친구목록"
                else-> tab.text = "친구 수락 대기"
            }
        }.attach()
    }
}