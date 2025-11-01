package com.epay.sbi.android.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.webkit.URLUtil
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 *
 *  Copyright (c) [2024] [State Bank of India]
 *  All rights reserved.
 *
 *   Author:@V1017566(Palash Gour)
 *  Version:1.0
 *
 */

/**
 * This class will test the all scenarios for payment url and network .
 */
class EPayValidationTest {

    private lateinit var mockContext: Context
    private lateinit var urlUtil: MockedStatic<URLUtil>
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var network: Network
    private lateinit var networkInfo: NetworkInfo
    private lateinit var networkCapabilities: NetworkCapabilities

    /**
     * Sets up the mock objects for testing.
     */
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mockContext = mock(Context::class.java)
        connectivityManager = mock(ConnectivityManager::class.java)
        network = mock(Network::class.java)
        networkInfo = mock(NetworkInfo::class.java)
        networkCapabilities = mock(NetworkCapabilities::class.java)
        `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
            .thenReturn(connectivityManager)
        urlUtil = Mockito.mockStatic(URLUtil::class.java)
        `when`(URLUtil.isValidUrl(anyString())).thenReturn(true)
    }

    /**
     * Tests the validatePaymentURL function for domain validation failure.
     */
    @Test
    fun validatePaymentURLDomainFail() {
        val url = Constants.TEST_PAYMENT_URL_DOMAIN_FAIL
        val result = EPayValidation.validatePaymentURL(url)
        assertEquals(false, result.valid)
        assertEquals(Constants.INVALID_DOMAIN, result.message)
    }

    /**
     * Tests the validatePaymentURL function for protocol validation failure.
     */
    @Test
    fun validatePaymentURLProtocolFail() {
        val url = Constants.TEST_PAYMENT_URL_PROTOCOL_FAIL
        val result = EPayValidation.validatePaymentURL(url)
        assertEquals(false, result.valid)
        assertEquals(Constants.INVALID_FORMAT, result.message)
    }

    /**
     * Tests the validatePaymentURL function for HASH validation failure.
     */
    @Test
    fun validatePaymentURLHashFail() {
        val url = Constants.TEST_PAYMENT_URL_HASH_FAIL
        val result = EPayValidation.validatePaymentURL(url)
        assertEquals(false, result.valid)
        assertEquals(Constants.INVALID_HASH, result.message)
    }

    /**
     * Tests the validatePaymentURL function for empty url.
     */
    @Test
    fun validatePaymentURLEmptyURL() {
        val url = ""  // An empty URL string
        val result = EPayValidation.validatePaymentURL(url)
        assertEquals(false, result.valid)
        assertEquals(Constants.INVALID_FORMAT, result.message)
    }

    /**
     * Tests the validatePaymentURL function for invalid url.
     */
    @Test
    fun validatePaymentURLInvalidURL() {
        `when`(URLUtil.isValidUrl(anyString())).thenReturn(false)
        val url = "szdfasdfa"
        val result = EPayValidation.validatePaymentURL(url)
        assertEquals(false, result.valid)
        assertEquals(Constants.INVALID_FORMAT, result.message)
    }

    /**
     * Tests the validatePaymentURL function for a successful validation.
     */
    @Test
    fun validatePaymentURLSuccess() {
        val url = Constants.TEST_PAYMENT_URL_SUCCESS
        val result = EPayValidation.validatePaymentURL(url)
        assertEquals(true, result.valid)
        assertEquals(Constants.VALID_PAYMENT_URL, result.message)
    }

    /**
     * Tests the validateNetworkConnectivity function for successful network connection.
     */
    @Test
    fun networkValidationConnectionSuccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            `when`(connectivityManager.activeNetwork).thenReturn(network)
            `when`(connectivityManager.getNetworkCapabilities(network))
                .thenReturn(networkCapabilities)
            `when`(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                .thenReturn(true)
        } else {
            `when`(connectivityManager.activeNetworkInfo).thenReturn(networkInfo)
            `when`(networkInfo.isConnected).thenReturn(true)
        }
        val isConnected = EPayValidation.validateNetworkConnectivity(mockContext)
        assertTrue(Constants.NETWORK_CONNECTION_AVAILABLE, isConnected)
    }

    /**
     * Tests the validateNetworkConnectivity function for network connection failure.
     */
    @Test
    fun networkValidationConnectionFailure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            `when`(connectivityManager.activeNetwork).thenReturn(network)
            `when`(connectivityManager.getNetworkCapabilities(network))
                .thenReturn(networkCapabilities)
            `when`(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                .thenReturn(false)
        } else {
            `when`(connectivityManager.activeNetworkInfo).thenReturn(networkInfo)
            `when`(networkInfo.isConnected).thenReturn(false)
        }
        val isConnected = EPayValidation.validateNetworkConnectivity(mockContext)
        assertFalse(Constants.NETWORK_CONNECTION_UNAVAILABLE, isConnected)
    }

    /**
     * Resource cleanup, after test execution.
     */
    @After
    fun tearDown() {
        urlUtil.close()
        Mockito.validateMockitoUsage()
    }

}