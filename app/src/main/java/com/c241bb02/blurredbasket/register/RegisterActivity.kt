package com.c241bb02.blurredbasket.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
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

        val spinner: Spinner = findViewById(R.id.planets_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.planets_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }


    private fun setupButtons() {
        with(binding) {
            registerBackButton.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}