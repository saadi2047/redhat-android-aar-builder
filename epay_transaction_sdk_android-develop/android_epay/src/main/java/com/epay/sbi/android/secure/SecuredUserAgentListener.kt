package com.epay.sbi.android.secure

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
 * Listener used for EpayWebView User Agent callback.
 */
interface SecuredUserAgentListener {
    /**
     * @param platformName name of the platform.
     */
    fun call(platformName: String)

}