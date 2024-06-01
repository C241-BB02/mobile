package com.c241bb02.blurredbasket.ui.onboarding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.c241bb02.blurredbasket.api.auth.LoginRequestDto
import com.c241bb02.blurredbasket.data.pref.UserModel
import com.c241bb02.blurredbasket.data.repository.UserRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(private val userRepository: UserRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    suspend fun login(dto: LoginRequestDto) {
        val response =  userRepository.login(dto)

        viewModelScope.launch {
            val user = UserModel(
                id = response.id ?: "",
                email = response.email ?: "",
                token = response.access ?: "",
                username = response.username ?: "",
                role = response.role ?: "",
            )
            userRepository.saveSession(user)
        }
    }
}