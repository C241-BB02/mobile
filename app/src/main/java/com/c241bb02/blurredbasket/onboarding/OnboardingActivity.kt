package com.c241bb02.blurredbasket.onboarding

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.databinding.ActivityOnboardingBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupButtons()
    }

    private fun setupStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue_100)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
    }

    private fun setupButtons() {
        with(binding) {
            loginButton.setOnClickListener {
                val dialog = BottomSheetDialog(this@OnboardingActivity, R.style.CustomBottomSheetDialog)
                dialog.setContentView(R.layout.login_dialog)
                dialog.show()
            }
        }
    }
}