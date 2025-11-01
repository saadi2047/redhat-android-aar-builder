package com.epay.sbi.android.secure

import com.epay.sbi.android.BuildConfig

/*
 *
 * Copyright (c) [2024] [State Bank of India]
 * All rights reserved.
 *
 * Author:@V1017516(RiteshShukla)
 * Version:1.0
 *
 */

/**
 * Class responsible for calling function used to set platform in EpayWebView User Agent.
 */
internal class SecuredUserAgent {

    private var listener: SecuredUserAgentListener? = null

    internal fun setListener(listener: SecuredUserAgentListener) {
        this.listener = listener
    }

    /**
     * Get key from respective platform, split the whole string and validate if the key is correct or not.
     * @param key string with required parameters in sequence of "EncryptedKey:MethodName:Platform"
     */
    internal fun call(key: String) {

        if (key.trim().isEmpty()) {
            return
        }

        try {

            val splitStr = key.split(":")
            val encryptedKey = splitStr[0]
            val methodName = splitStr[1]
            val platform = splitStr[2]

            if (encryptedKey == BuildConfig.sk && methodName == BuildConfig.mName) {
                fetchPlatform(platform)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * This function will be called to set platform field inside Transaction Activity listener callback.
     * @param platform Name of the platform that is required to set in EpayWebView User Agent.
     */
    private fun fetchPlatform(platform: String) {
        listener?.call(platform)
    }

}