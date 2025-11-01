package com.epay.sbi.android.utils

import com.epay.sbi.android.BuildConfig

/**
 * This class will store the constants of the app.
 */
object Constants {

    // URL constants (these are dynamic -> cannot be const)
    val HTTPS_PROTOCOL = BuildConfig.httpsProtocol
    val SBI_DOMAIN_DEV = BuildConfig.sbiDomainDev
    val SBI_DOMAIN_SIT = BuildConfig.sbiDomainSit
    val SBI_DOMAIN_UAT = BuildConfig.sbiDomainUat
    val SBI_DOMAIN_PREPROD = BuildConfig.sbiDomainPreProd
    val SBI_DOMAIN_PROD = BuildConfig.sbiDomainProd

    // Valid and Invalid error messages
    const val INVALID_DOMAIN = "Invalid domain in URL."
    const val INVALID_FORMAT = "Invalid URL format"
    const val INVALID_HASH = "Missing or incorrect payment hash."
    const val VALID_PAYMENT_URL = "Valid payment URL"

    // Payment status
    const val PAYMENT_STATUS_FAILED = 0

    // Network error messages
    const val NETWORK_CONNECTION_AVAILABLE = "Network connection available."
    const val NETWORK_CONNECTION_UNAVAILABLE =
        "Network connection unavailable. Please check your internet and try again."

    const val REFERRER_CANNOT_BE_EMPTY = "Referrer cannot be empty."

    // Payment page back events
    const val HOME = -1
    const val UPI = 0
    const val CARD = 1
    const val NET_BANKING = 2

    // Other messages
    const val SOMETHING_WENT_WRONG = "Something went wrong, please try again!"
    const val SUCCESS = "Success"
    const val REACT_NATIVE = "reactNative"
    const val ANDROID = "android"
    const val UI = "ui"
    const val EN = "en"

    /**
     * Used for testing purpose in TransactionActivityTest file
     */
    val TESTING_LOAD_URL = BuildConfig.testLoadUrl
    val TEST_PAYMENT_URL_PROTOCOL_FAIL = BuildConfig.testPaymentUrlProtocolFail
    val TEST_PAYMENT_URL_DOMAIN_FAIL = BuildConfig.testPaymentUrlDomainFail
    val TEST_PAYMENT_URL_HASH_FAIL = BuildConfig.testPaymentUrlHashFail
    val TEST_PAYMENT_URL_SUCCESS = BuildConfig.testPaymentUrlSuccess
}

