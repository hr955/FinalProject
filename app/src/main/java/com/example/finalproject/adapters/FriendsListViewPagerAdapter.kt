package com.example.finalproject.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.finalproject.fragments.MyFriendsListFragment
import com.example.finalproject.fragments.RequestedFriendListFragment

class FriendsListViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> MyFriendsListFragment.getFrag()
        else -> RequestedFriendListFragment.getFrag()
    }

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        0 -> "친구 목록"
        else -> "수락 대기"
    }
}