package com.example.finalproject.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.finalproject.R
import com.example.finalproject.web.ServerAPI
import com.example.finalproject.web.ServerAPIService
import retrofit2.Retrofit

abstract class BaseFragment : Fragment() {

    lateinit var mContext: Context

    private lateinit var retrofit: Retrofit
    lateinit var apiService: ServerAPIService

    lateinit var txtTitle: TextView
    lateinit var btnAdd: ImageView
    lateinit var btnFindFriend: ImageView

    // 뷰가 그려지는 시기
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setCustomActionBar()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    // 그려진 뷰에 데이터를 Set
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext = requireContext()
        retrofit = ServerAPI.getRetrofit(mContext)
        apiService = retrofit.create(ServerAPIService::class.java)

        setCustomActionBar()

    }

    abstract fun setupEvents()
    abstract fun setValues()

    private fun setCustomActionBar() {
        val defActionBar = (requireActivity() as AppCompatActivity).supportActionBar!!

        defActionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        defActionBar.setCustomView(R.layout.my_custom_action_bar)
        defActionBar.elevation = 0F

        txtTitle = defActionBar.customView.findViewById(R.id.txt_title)
        btnAdd = defActionBar.customView.findViewById(R.id.btn_add)
        btnFindFriend = defActionBar.customView.findViewById(R.id.btn_find_friend)
    }
}