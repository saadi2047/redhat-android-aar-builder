package com.epay.sbi.android.model

/**
 *
 *  Copyright (c) [2024] [State Bank of India]
 *  All rights reserved.
 *
 *  Author:@V1017704(Sandip Singh)
 *  Version:1.0
 *
 */

/**
 * This class will handle for validation result and display message.
 * @param valid whether the url is valid or invalid.
 * @param message validation message.
 */
data class ValidationData(
    val valid: Boolean,
    val message: String
)