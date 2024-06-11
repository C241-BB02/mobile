package com.c241bb02.blurredbasket.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.c241bb02.blurredbasket.R
import com.c241bb02.blurredbasket.api.auth.LoginErrorResponse
import com.c241bb02.blurredbasket.api.auth.LoginRequestDto
import com.c241bb02.blurredbasket.api.auth.RegisterErrorResponse
import com.c241bb02.blurredbasket.databinding.ActivityOnboardingBinding
import com.c241bb02.blurredbasket.ui.home.HomeActivity
import com.c241bb02.blurredbasket.ui.register.RegisterActivity
import com.c241bb02.blurredbasket.ui.utils.setupStatusBar
import com.c241bb02.blurredbasket.ui.view_model.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewModel: OnboardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = obtainViewModel(this)

        setDefaultBackBehavior()
        setupStatusBar(window, this, R.color.blue_100, true)
        observeUserState()
        setupButtons()
    }


    private fun setDefaultBackBehavior() {
        onBackPressedDispatcher.addCallback(this) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun observeUserState() {
        viewModel.getSession().observe(this) { user ->
            if (user.token != "") {
                val factory = ViewModelFactory.getInstance(this)
                factory.updateToken(user.token)
                moveToHomeScreen()
                finish()
            }
        }
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
        val usernameInput = view.findViewById<EditText>(R.id.usernameEditText)
        val passwordInput = view.findViewById<EditText>(R.id.passwordEditText)

        loginTriggerButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val dto = LoginRequestDto(
                username = username,
                password = password
            )

            if (loginInputIsValid(dto)) {
                lifecycleScope.launch {
                    try {
                        loginTriggerButton.isEnabled = false
                        viewModel.login(dto)
                        loginTriggerButton.isEnabled = true
                        dialog.dismiss()
                        showToast("Login successful!")
                    } catch (e: HttpException) {
                        loginTriggerButton.isEnabled = true
                        val errorBody = e.response()?.errorBody()?.string()
                        val gson = Gson()
                        val errorResponse: LoginErrorResponse? = gson.fromJson(errorBody, LoginErrorResponse::class.java)

                        if (errorResponse != null) {
                            errorResponse.detail?.let { message -> showToast(message) }
                        } else {
                            showToast("An error occurred while registering. Please try again.")
                        }
                    }
                }
            }
        }
    }

    private fun loginInputIsValid(dto: LoginRequestDto): Boolean {
        if (dto.username.isEmpty()) {
            showToast("Please input a username")
            return false
        }
        if (dto.password.isEmpty()) {
            showToast("Password cannot be empty")
            return false
        }
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun moveToRegisterScreen() {
        val moveIntent = Intent(this, RegisterActivity::class.java)
        startActivity(moveIntent)
    }

    private fun moveToHomeScreen() {
        val moveIntent = Intent(this, HomeActivity::class.java)
        startActivity(moveIntent)
    }

    private fun obtainViewModel(activity: AppCompatActivity): OnboardingViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[OnboardingViewModel::class.java]
    }
}