package com.epay.sbi.android.ui

import android.content.Context
import android.content.Intent
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.epay.sbi.android.R
import com.epay.sbi.android.interfaces.TransactionResultListener
import com.epay.sbi.android.model.PaymentResponseModel
import com.epay.sbi.android.utils.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Copyright (c) 2024 SBI. All rights reserved.
 */

/**
 * Testing class for TransactionActivity's
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TransactionActivityTest {

    private lateinit var scenario: ActivityScenario<TransactionActivity>

    private val resultListener = object : TransactionResultListener {
        override fun onSuccess(paymentResponseModel: PaymentResponseModel) {}
        override fun onFailed(paymentResponseModel: PaymentResponseModel) {}
        override fun onCancelled(paymentResponseModel: PaymentResponseModel) {}
        override fun onTimeOut(paymentResponseModel: PaymentResponseModel) {}
        override fun onError(paymentResponseModel: PaymentResponseModel) {}
        override fun onPending(paymentResponseModel: PaymentResponseModel) {}
    }

    @Before
    fun setUp() {
        Intents.init()
        TransactionActivity.setTransactionResultListener(resultListener)
    }

    @After
    fun tearDown() {
        Intents.release()
        if (::scenario.isInitialized) {
            scenario.close()
        }
    }

    @Test
    fun testIntentExtrasReceivedCorrectly() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, TransactionActivity::class.java).apply {
            putExtra("PAYMENT_URL", Constants.TEST_PAYMENT_URL_SUCCESS)
            putExtra("REFERRER", "testReferrer")
            putExtra("UA", "Android")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        scenario = ActivityScenario.launch(intent)

        // Verify that the activity started and received the correct data
        scenario.onActivity { activity ->
            assertEquals(
                Constants.TEST_PAYMENT_URL_SUCCESS,
                activity.intent.getStringExtra("PAYMENT_URL")
            )
            assertEquals("testReferrer", activity.intent.getStringExtra("REFERRER"))
            assertEquals("Android", activity.intent.getStringExtra("UA"))
        }
    }


    @Test
    fun testUrlIsLoadedInWebView() {

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            TransactionActivity::class.java
        ).apply {
            putExtra("PAYMENT_URL", Constants.TEST_PAYMENT_URL_SUCCESS)
            putExtra("REFERRER", "testReferrer")
            putExtra("UA", Constants.ANDROID)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Launch the activity
        val scenario = ActivityScenario.launch<TransactionActivity>(intent)

        // Find the WebView
        scenario.onActivity { activity ->

            val webView = activity.findViewById<WebView>(R.id.epayWebView)

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    val loadedUrl = view!!.url
                    assertEquals(Constants.TEST_PAYMENT_URL_SUCCESS, loadedUrl)

                }
            }

        }

    }

}