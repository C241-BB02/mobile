package com.c241bb02.blurredbasket.api.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotosItem(

	@field:SerializedName("image")
	val image: String = "",

	@field:SerializedName("product")
	val product: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("status")
	val status: String? = null
): Parcelable

@Parcelize
data class UserItem(
	@field:SerializedName("id")
	val id: String = "",

	@field:SerializedName("username")
	val username: String = "",
): Parcelable

@Parcelize
data class GetProductsResponseItem (

	@field:SerializedName("revenue")
	val revenue: Float? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("user")
	val user: UserItem? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("stock")
	val stock: Int? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("photos")
	val photos: List<PhotosItem> = emptyList(),
): Parcelable
