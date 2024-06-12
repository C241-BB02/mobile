package com.c241bb02.blurredbasket.ui.utils

import java.text.NumberFormat
import java.util.Currency

fun numberToRupiah(number: Int): String {
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 0
    format.currency = Currency.getInstance("IDR")

    return format.format(number)
}