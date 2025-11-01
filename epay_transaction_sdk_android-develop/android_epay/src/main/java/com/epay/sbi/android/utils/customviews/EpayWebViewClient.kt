package com.epay.sbi.android.utils.customviews

import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 *  Copyright (c) [2024] [State Bank of India]
 *  All rights reserved.
 *
 *  Author:@V1017704(Ritesh Shukla)
 *  Version:1.0
 *
 */

/**
 * Custom web view client for custom EpayWebView
 */
class EpayWebViewClient(
    private val epayWebView: EpayWebView,
    private val referrer: String,
    private val coroutineScope: CoroutineScope?,
) : WebViewClient() {

    private var isPageLoaded = false

    override fun onReceivedError(
        view: WebView?, request: WebResourceRequest?, error: WebResourceError?
    ) {

        coroutineScope?.launch {

            delay(15000)

            withContext(Dispatchers.Main) {
                epayWebView.loadUrl("javascript:window.location.reload( true )")
            }

        }

    }

    override fun shouldOverrideUrlLoading(
        webView: WebView?, request: WebResourceRequest?
    ): Boolean {

        webView?.let { view ->

            val url = request?.url.toString()

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = request?.url
            }

            val isAppInstalled = intent.resolveActivity(view.context.packageManager)

            if (url.startsWith("upi://")) {

                if (isAppInstalled == null) {

                    view.evaluateJavascript(
                        "javascript:onNoAppFound('')", null
                    )

                    return true

                } else {

                    Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                        view.context.startActivity(this)
                    }

                    return true

                }

            } else if (url.contains("://upi/pay")) {

                if (isAppInstalled == null) {

                    view.evaluateJavascript(
                        "javascript:onNoAppFound('')", null
                    )
                    return true

                } else {

                    Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                        view.context.startActivity(this)
                    }

                    return true

                }

            } else {

                view.loadUrl(url)
                return true

            }

        }

        return true

    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        if (!isPageLoaded) {
            isPageLoaded = true

            coroutineScope?.launch {
                while (true) {
                    view?.evaluateJavascript("javascript:onFetchReferrer('$referrer')") { result ->
                        if (result == "true") {
                            this.cancel()
                        }
                    }
                    delay(1000)
                }
            }

        }

    }
}
