package com.c241bb02.blurredbasket.api.auth

import com.google.gson.annotations.SerializedName

data class RegisterResponse (

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)
