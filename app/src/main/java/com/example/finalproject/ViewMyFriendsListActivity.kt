package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.finalproject.databinding.ActivityViewMyFriendsListBinding

class ViewMyFriendsListActivity : BaseActivity() {

    lateinit var binding: ActivityViewMyFriendsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_my_friends_list)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {
        TODO("Not yet implemented")
    }

    override fun setValues() {
        TODO("Not yet implemented")
    }
}