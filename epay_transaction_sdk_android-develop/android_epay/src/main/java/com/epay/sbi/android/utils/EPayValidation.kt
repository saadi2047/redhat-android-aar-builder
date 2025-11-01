package com.epay.sbi.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.webkit.URLUtil
import com.epay.sbi.android.model.ValidationData
import com.epay.sbi.android.utils.Constants.HTTPS_PROTOCOL
import com.epay.sbi.android.utils.Constants.SBI_DOMAIN_DEV
import com.epay.sbi.android.utils.Constants.SBI_DOMAIN_PREPROD
import com.epay.sbi.android.utils.Constants.SBI_DOMAIN_PROD
import com.epay.sbi.android.utils.Constants.SBI_DOMAIN_SIT
import com.epay.sbi.android.utils.Constants.SBI_DOMAIN_UAT
import java.net.URL

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
 * This class handles all the methods for validating the Payment URL.
 */
internal object EPayValidation {

    /**
     * Validates the payment URL and network connectivity.
     * @param context required for using ConnectivityManager from system service.
     * @param paymentUrl The URL to be validated.
     * @return ValidationData class for knowing whether the validation is success/failed with message.
     */
    fun validatePaymentUrlAndConnectivity(
        context: Context,
        paymentUrl: String,
        referrer: String
    ): ValidationData {

        if (referrer.trim().isEmpty()) {
            return ValidationData(false, Constants.REFERRER_CANNOT_BE_EMPTY)
        }

        val isPaymentURLValid = validatePaymentURL(paymentUrl)
        if (isPaymentURLValid.valid.not()) {
            return isPaymentURLValid
        }

        if (validateNetworkConnectivity(context).not()) {
            return ValidationData(false, Constants.NETWORK_CONNECTION_UNAVAILABLE)
        }

        return ValidationData(true, Constants.SUCCESS)

    }

    /**
     * Validates the payment URL.
     * @param url The URL to be validated.
     * @return ValidationData class for knowing whether the url is valid/invalid and error message.
     */
    fun validatePaymentURL(url: String): ValidationData {

        // TODO: Do required changes before going to production

        if (url.trim().isEmpty() || !URLUtil.isValidUrl(url)) {
            return ValidationData(false, Constants.INVALID_FORMAT)
        }

        val domain = URL(url).host
        val isNotHttps = url.startsWith(HTTPS_PROTOCOL).not()
        val isInValidDomain = domain != null &&
                (domain == SBI_DOMAIN_DEV || domain == SBI_DOMAIN_SIT || domain == SBI_DOMAIN_UAT || domain == SBI_DOMAIN_PREPROD || domain == SBI_DOMAIN_PROD).not()
        val isInvalidHash = validateUrlHash(url).not()

        return when {
            isNotHttps -> ValidationData(false, Constants.INVALID_FORMAT)
            isInValidDomain -> ValidationData(false, Constants.INVALID_DOMAIN)
            isInvalidHash -> ValidationData(false, Constants.INVALID_HASH)
            else -> ValidationData(true, Constants.VALID_PAYMENT_URL)
        }

    }

    /**
     * Validates the HASH format.
     * @param url The URL from which the hash will be extracted and validated.
     * @return true if the hash is valid else false.
     */
    private fun validateUrlHash(url: String): Boolean {

        // TODO: Do required changes before going to production

        val version = url.substring(url.lastIndexOf('/') - 2, url.lastIndexOf('/'))

        if (version != Constants.UI) {
            return false
        }

        val hashValue = url.substring(url.lastIndexOf('/') + 1)
        val hashNotEmpty = hashValue.trim().isNotEmpty()
        val isLengthValid = hashValue.length > 1
        val isContainsHash = hashValue.substring(0, 1).contains("#")

        return hashNotEmpty && isLengthValid && isContainsHash

    }

    /**
     * Validates the network connectivity status.
     * @param context The application context used to retrieve the ConnectivityManager.
     * @return true if the device is connected to the internet else false.
     */
    @SuppressLint("ObsoleteSdkInt")
    fun validateNetworkConnectivity(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
}