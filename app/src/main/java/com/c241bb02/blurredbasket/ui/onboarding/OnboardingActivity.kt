package com.c241bb02.blurredbasket.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.databinding.ActivityOnboardingBinding
import com.c241bb02.blurredbasket.ui.home.HomeActivity
import com.c241bb02.blurredbasket.ui.register.RegisterActivity
import com.c241bb02.blurredbasket.ui.utils.setupStatusBar
import com.google.android.material.bottomsheet.BottomSheetDialog

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar(window, this, R.color.blue_100, true)
        setupButtons()
    }


    private fun setupButtons() {
        with(binding) {
            loginButton.setOnClickListener {
                val dialog = BottomSheetDialog(this@OnboardingActivity, R.style.CustomBottomSheetDialog)
                val view = LayoutInflater.from(this@OnboardingActivity).inflate(R.layout.login_dialog, null)
                dialog.setContentView(view)
                setupLoginDialog(dialog, view)
                dialog.show()
            }

            registerButton.setOnClickListener {
                moveToRegisterScreen()
            }
        }
    }

    private fun setupLoginDialog(dialog: BottomSheetDialog, view: View) {
        val loginTriggerButton = view.findViewById<Button>(R.id.login_trigger_button)
        val emailInput = view.findViewById<EditText>(R.id.emailEditText)
        val passwordInput = view.findViewById<EditText>(R.id.passwordEditText)

        loginTriggerButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            // TODO: hit backend
            moveToHomeScreen()
            dialog.dismiss()
        }
    }

    private fun moveToRegisterScreen() {
        val moveIntent = Intent(this, RegisterActivity::class.java)
        startActivity(moveIntent)
    }

    private fun moveToHomeScreen() {
        val moveIntent = Intent(this, HomeActivity::class.java)
        startActivity(moveIntent)
    }
}