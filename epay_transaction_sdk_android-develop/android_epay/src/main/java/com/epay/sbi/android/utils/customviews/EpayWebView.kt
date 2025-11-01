package com.epay.sbi.android.utils.customviews

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView
import com.epay.sbi.android.BuildConfig
import kotlinx.coroutines.CoroutineScope

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
 * Custom EpayWebView for loading Payment Url page.
 */
class EpayWebView : WebView {

    /**
     * Constructor for creating custom webview
     * @param context required for creating custom view
     */
    constructor(context: Context) : super(context) {
        setEpayWebViewSettings()
    }

    /**
     * Constructor for creating custom webview with AttributeSet parameter
     * @param context required for creating custom view
     * @param attrs for reading custom properties of view from xml/layout file
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setEpayWebViewSettings()
    }

    /**
     * To set EpayWebview settings
     */
    private fun setEpayWebViewSettings() {
        with(this.settings) {
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            domStorageEnabled = true
            clearHistory()

            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
        }
    }

    /**
     * For initializing the EpayWebView.
     * @param paymentUrl URL to be loaded inside EpayWebView.
     * @param platform Name of the platform.
     * @param referrer Referrer header to be used.
     * @param onPageLoadCompleted Callback to be called when the page finishes loading.
     */
    fun initWebView(
        paymentUrl: String,
        platform: String,
        referrer: String,
        coroutineScope: CoroutineScope,
        onPageLoadCompleted: () -> Unit
    ) {

        setUserAgent(platform)

        // Set up the WebChromeClient with the JsInterface
        webChromeClient = EpayWebChromeClient(referrer, onPageLoadCompleted, coroutineScope)

        // Set up the WebViewClient (can be customized further if needed)
        webViewClient = EpayWebViewClient(this, referrer, coroutineScope)

        // Load the payment URL
        loadUrl(paymentUrl)

    }

    /**
     * For setting EpayWebView user agent.
     * @param platform Name of the platform.
     */
    private fun setUserAgent(platform: String) {

        val platformName = if (platform.trim().isNotEmpty()) {
            platform
        } else {
            "android"
        }

        val defaultUserAgent = WebView(context).settings.userAgentString

        settings.userAgentString = defaultUserAgent + " " +
                "ePay-SDK-$platformName-FE-v${BuildConfig.version_name}"

    }
}
