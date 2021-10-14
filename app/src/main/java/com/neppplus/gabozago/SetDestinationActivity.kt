package com.neppplus.gabozago

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.neppplus.gabozago.databinding.ActivitySetDestinationBinding

lateinit var binding: ActivitySetDestinationBinding

class SetDestinationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_destination)
    }

    override fun setupEvents() {
    }

    override fun setValues() {
    }
}