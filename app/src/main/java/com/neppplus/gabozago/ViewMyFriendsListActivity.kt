package com.neppplus.gabozago

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.neppplus.gabozago.adapters.FriendsListViewPagerAdapter
import com.neppplus.gabozago.databinding.ActivityViewMyFriendsListBinding

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
//        btnAdd.setOnClickListener {
//            startActivity(Intent(mContext, FindFriendActivity::class.java))
//        }
//
//        binding.vpFriends.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
//            override fun onPageScrolled(
//                position: Int,
//                positionOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//            }
//
//            override fun onPageSelected(position: Int) {
//                when(position){
//                    0-> (mAdapter.getItem(position) as MyFriendsListFragment).getMyFriendsListFromServer()
//                    else -> (mAdapter.getItem(position) as RequestedFriendListFragment).getRequestFriendListFromServer()
//                }
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {
//            }
//        })
    }

    override fun setValues() {
//        txtTitle.text = "친구 목록 관리"
//        mAdapter = FriendsListViewPagerAdapter(supportFragmentManager)
//        binding.vpFriends.adapter = mAdapter
//        binding.tlFriends.setupWithViewPager(binding.vpFriends)
//        btnAdd.visibility = View.VISIBLE
    }
}