package com.c241bb02.blurredbasket.ui.register

import androidx.lifecycle.ViewModel
import com.c241bb02.blurredbasket.api.auth.RegisterRequestDto
import com.c241bb02.blurredbasket.api.auth.RegisterResponse
import com.c241bb02.blurredbasket.data.repository.UserRepository

class RegisterViewModel(private val userRepository: UserRepository): ViewModel() {
    suspend fun register(dto: RegisterRequestDto): RegisterResponse {
        return userRepository.register(dto)
    }
}