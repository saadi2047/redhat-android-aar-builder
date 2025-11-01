package com.epay.sbi.android.interfaces

import android.webkit.JavascriptInterface
import com.epay.sbi.android.model.PaymentResponseModel
import com.epay.sbi.android.utils.customviews.EpayWebView
import org.json.JSONObject
import java.lang.ref.WeakReference

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
 * Class contains the methods that can be called by web.
 * The methods contains the action that needs to be performed when they are called.
 * @param epayWebView EpayWebView instance to call javascript function.
 * @param transactionResultListener listener for success, failed and cancelled callbacks, passed from sdk to merchant app.
 */
internal class EpayJsInterface(
    private val epayWebView: EpayWebView,
    private val transactionResultListener: WeakReference<TransactionResultListener>? = null,
    private val jsListener: JsListener? = null,
    private val onPopUpClosedCallback: ((String) -> Unit) = {}
) {

    /**
     * For handling the response from JS to close EpayWebView.
     * @param json payment response with all other details.
     */
    @JavascriptInterface
    fun onCloseEpay(json: String) {
        transactionResultListener?.get()?.onCancelled(PaymentResponseModel.parseJson(json))
        jsListener?.onActivityFinish()
    }

    /**
     * For handling the response from JS for success event.
     * @param json payment response with all other details.
     */
    @JavascriptInterface
    fun onSuccess(json: String) {
        transactionResultListener?.get()?.onSuccess(PaymentResponseModel.parseJson(json))
        jsListener?.onActivityFinish()
    }

    /**
     * For handling the response from JS for failed event.
     * @param json payment response with all other details.
     */
    @JavascriptInterface
    fun onFailed(json: String) {
        transactionResultListener?.get()?.onFailed(PaymentResponseModel.parseJson(json))
        jsListener?.onActivityFinish()
    }

    /**
     * This class handled the PaymentResponseModel response and ThemeModel response
     * Converts ThemeModel to PaymentResponseModel and sends to onCancelled
     * @param json payment response with all other details.
     */
    @JavascriptInterface
    fun onCancelled(json: String) {
        transactionResultListener?.get()?.onCancelled(PaymentResponseModel.parseJson(json))
        jsListener?.onActivityFinish()
    }

    /**
     * For handling the response from JS for cancelled event.
     * @param json payment response with all other details.
     */
    @JavascriptInterface
    fun onError(json: String) {
        transactionResultListener?.get()?.onError(PaymentResponseModel.parseJson(json))
        jsListener?.onActivityFinish()
    }

    /**
     * For handling the response from JS for pending event.
     * @param json payment response with all other details.
     */
    @JavascriptInterface
    fun onPending(json: String) {
        transactionResultListener?.get()?.onPending(PaymentResponseModel.parseJson(json))
        jsListener?.onActivityFinish()
    }

    /**
     * For handling the response from JS for time-out event.
     * @param json payment response with all other details.
     */
    @JavascriptInterface
    fun onTimeOut(json: String) {
        transactionResultListener?.get()?.onTimeOut(PaymentResponseModel.parseJson(json))
        jsListener?.onActivityFinish()
    }

    /**
     * For triggering the auto read SMS flow from website end.
     */
    @JavascriptInterface
    fun onAutoReadSMS() {
        jsListener?.initAutoReadSMS()
    }

    /**
     * For closing the new popup window.
     */
    @JavascriptInterface
    fun onPopUpClosed(data: String) {
        onPopUpClosedCallback(data)
    }


    /**
     * Called from JavaScript to send updated back-handling configuration.
     *
     */
    @JavascriptInterface
    fun onDeviceBackPress(json: String) {
        jsListener?.onJsBackEvent(json)
    }

    /**
     * This method parses the JSON and updates the ThemeViewModel.
     *
     */
    @JavascriptInterface
    fun onInit(json: String) {
        jsListener?.onInitEvent(json)
    }

    /**
     * This method extracts the language code and updates the ViewModel.
     *
     */
    @JavascriptInterface
    fun onSetLanguage(json: String) {
        val jsonObject = JSONObject(json)
        val lang = jsonObject.optString("languageType", "en")
        jsListener?.onSetLanguage(lang)
    }

}