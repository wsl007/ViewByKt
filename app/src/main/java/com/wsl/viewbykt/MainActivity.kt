package com.wsl.viewbykt

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.wsl.viewbykt.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btn.setOnClickListener {
            startActivity(Intent(this@MainActivity, TestActivity::class.java))
        }

        pET.postDelayed({
            pET.useFloatLabel = false
            pET.postDelayed({
                pET.useFloatLabel = true
            }, 5000)
        }, 3000)
    }
}