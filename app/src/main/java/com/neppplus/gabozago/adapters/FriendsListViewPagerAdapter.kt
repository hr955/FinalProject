package com.neppplus.gabozago.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.neppplus.gabozago.fragments.MyFriendsListFragment
import com.neppplus.gabozago.fragments.RequestedFriendListFragment

class FriendsListViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> MyFriendsListFragment()
            else -> RequestedFriendListFragment()
        }
}