package com.c241bb02.blurredbasket.ui.register

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.api.auth.RegisterRequestDto
import com.c241bb02.blurredbasket.api.auth.RegisterResponse
import com.c241bb02.blurredbasket.databinding.ActivityRegisterBinding
import com.c241bb02.blurredbasket.ui.utils.setupStatusBar
import com.c241bb02.blurredbasket.ui.view_model.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = obtainViewModel(this)

        setupStatusBar(window, this, R.color.blue_50, true)
        setupButtons()
    }


    private fun setupButtons() {
        with(binding) {
            registerBackButton.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            registerTriggerButton.setOnClickListener {
                val username = registerUsernameInput.text.toString().trim()
                val email = registerEmailInput.text.toString().trim()
                val password = registerPasswordInput.text.toString().trim()
                val confirmPassword = registerConfirmPasswordInput.text.toString().trim()
                var role = ""
                when (registerRoleRadioGroup.checkedRadioButtonId) {
                    R.id.register_customer_radio_button -> {
                        role = "CUSTOMER"
                    }R.id.register_seller_radio_button -> {
                        role = "SELLER"
                    }
                }
                val dto = RegisterRequestDto(
                    username = username,
                    email = email,
                    password = password,
                    role = "ADMIN"
                )

                if (registerInputIsValid(dto, confirmPassword)) {
                   register(dto)
                }
            }
        }
    }

    private fun register(dto: RegisterRequestDto) {
        lifecycleScope.launch {
            try {
                viewModel.register(dto)
                showToast("Register successful!")
                onBackPressedDispatcher.onBackPressed()
            } catch (e: HttpException) {
                showToast("An error occurred while registering. Please try again.")
            }
        }
    }

    private fun registerInputIsValid(dto: RegisterRequestDto, confirmPassword: String): Boolean {
        if (dto.role.isEmpty()) {
            showToast("Please input a role")
            return false
        }

        if (dto.username.isEmpty()) {
            showToast("Username cannot be empty")
            return false
        }

        if (!isValidEmail(dto.email)) {
            showToast("Invalid email format")
            return false
        }

        if (dto.password.isEmpty()) {
            showToast("Password cannot be empty")
            return false
        }

        if (dto.password != confirmPassword) {
            showToast("Password and confirm password do not match")
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showToast(message: String) {
        val contextView = findViewById<View>(R.id.register_trigger_button)
        Snackbar.make(contextView, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun obtainViewModel(activity: AppCompatActivity): RegisterViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[RegisterViewModel::class.java]
    }
}