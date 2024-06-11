package com.c241bb02.blurredbasket.ui.register

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.api.auth.RegisterErrorResponse
import com.c241bb02.blurredbasket.api.auth.RegisterRequestDto
import com.c241bb02.blurredbasket.databinding.ActivityRegisterBinding
import com.c241bb02.blurredbasket.ui.utils.setupStatusBar
import com.c241bb02.blurredbasket.ui.view_model.ViewModelFactory
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
                    role = role
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
                binding.registerTriggerButton.isEnabled = false
                viewModel.register(dto)
                binding.registerTriggerButton.isEnabled = true
                showToast("Register successful!")
                onBackPressedDispatcher.onBackPressed()
            } catch (e: HttpException) {
                binding.registerTriggerButton.isEnabled = true
                val errorBody = e.response()?.errorBody()?.string()
                val gson = Gson()
                val errorResponse: RegisterErrorResponse? = gson.fromJson(errorBody, RegisterErrorResponse::class.java)

                if (errorResponse != null) {
                    if (errorResponse.username != null) {
                        errorResponse.username[0]?.let { showToast(it.replaceFirstChar { char -> char.uppercase() }) }
                    } else if( errorResponse.email != null) {
                        errorResponse.email[0]?.let { showToast(it.replaceFirstChar { char -> char.uppercase() }) }
                    }
                } else {
                    showToast("An error occurred while registering. Please try again.")
                }
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun obtainViewModel(activity: AppCompatActivity): RegisterViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[RegisterViewModel::class.java]
    }
}