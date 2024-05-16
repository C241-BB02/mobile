package com.c241bb02.blurredbasket.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.databinding.ActivityRegisterBinding
import com.c241bb02.blurredbasket.utils.setupStatusBar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar(window, this, R.color.blue_50, true)
        setupButtons()
    }


    private fun setupButtons() {
        with(binding) {
            registerBackButton.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}