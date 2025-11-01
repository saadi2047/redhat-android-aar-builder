package com.epay.sbi.android.utils

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import androidx.core.provider.FontRequest
import androidx.core.provider.FontsContractCompat
import com.epay.sbi.android.R

/**
 *
 *  Copyright (c) [2024] [State Bank of India]
 *  All rights reserved.
 *
 *  Author:@V1017566(Palash Gour)
 *  Version:1.0
 *
 */
/**
 * FontHelper is a utility singleton for loading downloading fonts.
 * from Google play Services (GMS) with caching support.
 */
object FontHelper {

    private val cache = mutableMapOf<String, Typeface>()

    fun loadFont(
        context: Context,
        fontQuery: String,
        onLoaded: (Typeface?) -> Unit
    ) {
        // Return cached font if available
        cache[fontQuery]?.let {
            onLoaded(it)
            return
        }

        val fontRequest = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            fontQuery,
            R.array.com_google_android_gms_fonts_certs
        )

        // Use a handler on the main looper
        val handler = Handler(Looper.getMainLooper())

        FontsContractCompat.requestFont(
            context,
            fontRequest,
            object : FontsContractCompat.FontRequestCallback() {
                override fun onTypefaceRetrieved(typeface: Typeface) {
                    cache[fontQuery] = typeface
                    onLoaded(typeface)
                }

                override fun onTypefaceRequestFailed(reason: Int) {
                    onLoaded(null)
                }
            },
            handler
        )
    }
}