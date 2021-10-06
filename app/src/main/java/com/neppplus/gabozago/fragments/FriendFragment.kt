package com.neppplus.gabozago.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.neppplus.gabozago.FindFriendActivity
import com.neppplus.gabozago.R
import com.neppplus.gabozago.adapters.FriendsListViewPagerAdapter
import com.neppplus.gabozago.databinding.FragmentFriendBinding
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
        binding.vpFriend.adapter = FriendsListViewPagerAdapter(this)

        TabLayoutMediator(binding.tabFriend, binding.vpFriend) { tab, position ->
            when(position){
                0-> tab.text = "친구목록"
                else-> tab.text = "친구 수락 대기"
            }
        }.attach()
    }
}