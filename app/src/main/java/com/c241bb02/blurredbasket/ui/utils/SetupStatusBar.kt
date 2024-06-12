package com.c241bb02.blurredbasket.ui.utils

import android.content.Context
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat

fun setupStatusBar(window: Window, context: Context, color: Int, isLightMode: Boolean) {
    window.statusBarColor = ContextCompat.getColor(context, color)
    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = isLightMode
    }
}