package com.epay.sbi.android.utils.customviews

import android.app.Dialog
import android.graphics.Color
import android.os.Message
import android.view.KeyEvent
import android.view.LayoutInflater
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebView.WebViewTransport
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.epay.sbi.android.R
import com.epay.sbi.android.interfaces.EpayJsInterface
import com.epay.sbi.android.utils.CommonUtils
import com.epay.sbi.android.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
 * Custom web chrome client for custom EpayWebView
 */
class EpayWebChromeClient(
    private val referrer: String,
    private val onPageLoadCompleted: () -> Unit,
    private val coroutineScope: CoroutineScope,
) : WebChromeClient() {
    /**
     * To get the web page loading progress.
     * @param view instance of webview.
     * @param newProgress latest web page loading progress value.
     */
    override fun onProgressChanged(
        view: WebView?, newProgress: Int
    ) {
        super.onProgressChanged(view, newProgress)

        if (newProgress == 100) {
            onPageLoadCompleted()
        }

    }

    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {

        view?.let {

            val context = it.context

            val dialogView = LayoutInflater.from(context).inflate(
                R.layout.dialog_webview_popup, null
            )

            val dialog = Dialog(context, R.style.FullScreenDialog)
            dialog.setContentView(dialogView)

            dialog.window?.let { window ->
                val color = ContextCompat.getColor(context, R.color.status_bar_color)
                CommonUtils.setStatusBarColor(window, color)
            }

            val closePopup = dialogView.findViewById<ImageView>(R.id.close_popup)
            val nWebView = dialogView.findViewById<EpayWebView>(R.id.epayWebView)

            nWebView.setBackgroundColor(Color.WHITE)

            nWebView.addJavascriptInterface(

                EpayJsInterface(nWebView, null, null) { webViewData ->

                    coroutineScope.launch {

                        dialog.dismiss()

                        view.evaluateJavascript(
                            "javascript:onPopUpClosed('$webViewData')", null
                        )

                    }

                },

                Constants.ANDROID
            )

            dialog.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    dismissDialog(view, dialog)
                    true // consume the event
                } else {
                    false // let other keys pass through
                }
            }

            closePopup.setOnClickListener {
                dismissDialog(view, dialog)
            }

            nWebView.settings.userAgentString = view.settings.userAgentString

            nWebView.webViewClient = EpayWebViewClient(nWebView, referrer, coroutineScope)

            dialog.show()

            (resultMsg?.obj as WebViewTransport).webView = nWebView
            resultMsg.sendToTarget()

        }

        return true

    }

    private fun dismissDialog(view: WebView, dialog: Dialog) {
        val response = "{\"message\":\"Cancelled\",\"res\":\"SDKCancelled\"}"
        view.evaluateJavascript(
            "javascript:onPopUpClosed('$response')", null
        )
        dialog.dismiss()
    }


}
