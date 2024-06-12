package com.c241bb02.blurredbasket.api.auth

data class RegisterRequestDto (
	val password: String,
	val role: String,
	val email: String,
	val username: String
)
