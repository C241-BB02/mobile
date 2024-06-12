package com.c241bb02.blurredbasket.api.auth

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("access")
	val access: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("refresh")
	val refresh: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
