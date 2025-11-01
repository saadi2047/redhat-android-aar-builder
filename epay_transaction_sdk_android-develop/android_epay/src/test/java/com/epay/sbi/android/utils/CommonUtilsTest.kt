package com.epay.sbi.android.utils

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

/*
 *
 * Copyright (c) [2024] [State Bank of India]
 * All rights reserved.
 *
 * Author:@V1017516(RiteshShukla)
 * Version:1.0
 *
 */


class CommonUtilsTest {

    /**
     * Sets up the mock objects for testing.
     */
    @Before
    fun setUp() {

    }

    /**
     * Tests the FetchVerificationCode function for successful reading OTP from given SMS.
     */
    @Test
    fun testFetchVerificationCodeSuccess() {

        val smsMessage =
            "Your OTP for the transaction is 122333. Do not share with anyone for security reason."

        val otpCode = CommonUtils.fetchVerificationCode(smsMessage)

        assertEquals("122333", otpCode)

    }

    /**
     * Tests the FetchVerificationCode function for failure in reading OTP from given SMS.
     */
    @Test
    fun testFetchVerificationCodeFailure() {

        val smsMessage =
            "Your OTP for the transaction is something. Do not share with anyone for security reason."

        val otpCode = CommonUtils.fetchVerificationCode(smsMessage)

        assertEquals("", otpCode)

    }

    /**
     * Tests the FetchVerificationCode function for successful reading OTP length from given SMS.
     */
    @Test
    fun testFetchVerificationCodeLengthSuccess() {

        val smsMessage =
            "Your OTP for the transaction is 122333. Do not share with anyone for security reason."

        val otpCode = CommonUtils.fetchVerificationCode(smsMessage)

        assertEquals(6, otpCode.length)

    }

    /**
     * Tests the FetchVerificationCode function for failure in reading OTP length from given SMS.
     */
    @Test
    fun testFetchVerificationCodeLengthFailure() {

        val smsMessage =
            "Your OTP for the transaction is 12233. Do not share with anyone for security reason."

        val otpCode = CommonUtils.fetchVerificationCode(smsMessage)

        assertNotEquals(6, otpCode.length)

    }

    /**
     * Tests the FetchVerificationCode function with multiple OTP present in given SMS.
     */
    @Test
    fun testFetchVerificationCodeTwice() {

        val smsMessage =
            "Your OTP for the transaction is 123456. Do not share with anyone 122333 for security reason."

        val otpCode = CommonUtils.fetchVerificationCode(smsMessage)

        assertEquals("123456", otpCode)

    }

    /**
     * Resource cleanup, after test execution.
     */
    @After
    fun tearDown() {

    }

}