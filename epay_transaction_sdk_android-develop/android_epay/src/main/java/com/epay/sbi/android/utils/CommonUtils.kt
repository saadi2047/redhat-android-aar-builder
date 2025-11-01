package com.epay.sbi.android.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 *
 *  Copyright (c) [2024] [State Bank of India]
 *  All rights reserved.
 *
 *  Author:@V1017704(Palash Gour)
 *  Version:1.0
 *
 */

/**
 * Contains the constants and utility methods.
 */
internal object CommonUtils {

    /**
     * For getting the 6 digits otp from the complete SMS message.
     * @param message complete SMS message.
     * @return 6 digits otp code from SMS message.
     */
    fun fetchVerificationCode(message: String): String {
        return Regex("(\\d{6})").find(message)?.value ?: ""
    }

    /**
     * For getting the theme color for dialog box.
     * @param color theme color.
     */
    private fun isColorDark(color: String): Boolean {
        val hex = color.replace("#", "").lowercase()

        val fullHex = when (hex.length) {
            3 -> hex.map { "$it$it" }.joinToString("") // Expand shorthand like "abc" → "aabbcc"
            6 -> hex
            else -> throw IllegalArgumentException("Invalid hex color format: $color")
        }

        val r = fullHex.substring(0, 2).toInt(16)
        val g = fullHex.substring(2, 4).toInt(16)
        val b = fullHex.substring(4, 6).toInt(16)

        val luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255

        return luminance < 0.5
    }

    /**
     * For getting the text color for dialog box.
     * @param themeColor text color.
     */
    fun getTextColor(themeColor: String): Int {
        return if (isColorDark(themeColor)) Color.WHITE else Color.BLACK
    }

    fun isColorLight(color: Int): Boolean {
        val luminance =
            (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return luminance > 0.5
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun covertHexColorToString(context: Context, @ColorRes color: Int): String {
        val colorString = ContextCompat.getColor(context, color)
        return String.format("#%06X", colorString and 0x00FFFFFF)
    }

    /**
     * Method for handling the status bar color.
     * @param window width of the screen.
     * @param color indicate the color for status bar
     */
    fun setStatusBarColor(window: Window, color: Int) {
        val decorView = window.decorView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30+
            decorView.setOnApplyWindowInsetsListener { view, insets ->
                val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
                view.setPadding(0, 0, 0, 0)
                view.setBackgroundColor(color)

                view.post {
                    val isLight = isColorLight(color)
                    window.insetsController?.setSystemBarsAppearance(
                        if (isLight) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                }
                insets
            }
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, false)

            val decorView = window.decorView

            ViewCompat.setOnApplyWindowInsetsListener(decorView) { view, insets ->
                val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())

                view.setPadding(0, statusBarInsets.top, 0, 0) // Apply status bar inset padding
                view.setBackgroundColor(color) // Set background color

                val isLight = CommonUtils.isColorLight(color)
                val controller = WindowInsetsControllerCompat(window, view)
                controller.isAppearanceLightStatusBars = isLight // Adjust the status bar icons

                insets
            }

            decorView.setBackgroundColor(color)
        }

        decorView.setBackgroundColor(color)
    }

}