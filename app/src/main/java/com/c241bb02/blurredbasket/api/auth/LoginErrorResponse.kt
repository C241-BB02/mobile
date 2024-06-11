package com.c241bb02.blurredbasket.api.auth

import com.google.gson.annotations.SerializedName

data class LoginErrorResponse (

	@field:SerializedName("detail")
	val detail: String? = null
)
