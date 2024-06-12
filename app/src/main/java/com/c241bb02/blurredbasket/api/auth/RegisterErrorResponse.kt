package com.c241bb02.blurredbasket.api.auth

import com.google.gson.annotations.SerializedName

data class RegisterErrorResponse (

	@field:SerializedName("email")
	val email: List<String?>? = null,

	@field:SerializedName("username")
	val username: List<String?>? = null
)
