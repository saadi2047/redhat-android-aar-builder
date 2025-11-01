package com.epay.sbi.android.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.epay.sbi.android.R
import com.epay.sbi.android.interfaces.EpayJsInterface
import com.epay.sbi.android.interfaces.JsListener
import com.epay.sbi.android.interfaces.TransactionResultListener
import com.epay.sbi.android.model.PaymentResponseModel
import com.epay.sbi.android.model.ThemeModel
import com.epay.sbi.android.secure.SecuredUserAgent
import com.epay.sbi.android.secure.SecuredUserAgentListener
import com.epay.sbi.android.utils.CommonUtils
import com.epay.sbi.android.utils.CommonUtils.covertHexColorToString
import com.epay.sbi.android.utils.Constants
import com.epay.sbi.android.utils.Constants.PAYMENT_STATUS_FAILED
import com.epay.sbi.android.utils.EPayValidation
import com.epay.sbi.android.utils.FontHelper
import com.epay.sbi.android.utils.customviews.EpayWebView
import com.epay.sbi.android.viewmodel.TransactionViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
 * This class is opened when merchant invokes the SDK.
 * It contains the webView which loads the payment page.
 */
class TransactionActivity : AppCompatActivity(), SecuredUserAgentListener, JsListener {

    private lateinit var transactionViewModel: TransactionViewModel

    private lateinit var epayWebView: EpayWebView
    private lateinit var progressBar: ProgressBar

    private var themeState: ThemeModel? = null

    private var platform = ""
    private var receivedOTP = ""
    private var initOtpReceiver = false
    private var referrer = ""
    private var selectedLanguage: String = Constants.EN

    /**
     * For fetching OTP from SMS and to allow/deny using activity result callback.
     */
    private var resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {

            // Consent given. OTP will be filled inside field automatically.
            val data: Intent = result.data!!
            val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)!!

            val otpCode = CommonUtils.fetchVerificationCode(message)
            receivedOTP = otpCode

        }

    }

    /**
     * For receiving SMS action with data based on the status.
     */
    private val smsVerificationReceiver = object : BroadcastReceiver() {

        @SuppressLint("UnsafeIntentLaunch")
        override fun onReceive(context: Context, intent: Intent) {

            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {

                val smsRetrieverStatus = IntentCompat.getParcelableExtra(
                    intent, SmsRetriever.EXTRA_STATUS, Status::class.java
                )

                when (smsRetrieverStatus?.statusCode) {

                    CommonStatusCodes.SUCCESS -> {

                        // Get consent intent
                        val consentIntent = IntentCompat.getParcelableExtra(
                            intent, SmsRetriever.EXTRA_CONSENT_INTENT, Intent::class.java
                        )

                        try {

                            // Show consent dialog to user, must be started in 5 minutes, otherwise you'll receive another TIMEOUT intent
                            resultLauncher.launch(consentIntent)

                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                        }

                    }

                    CommonStatusCodes.TIMEOUT -> {
                        // Time out occurred, handle the error.
                    }

                }

            }

        }

    }

    companion object {

        private const val KEY_PAYMENT_URL = "PAYMENT_URL"
        private const val KEY_REFERRER = "REFERRER"

        private var transactionResultListener: WeakReference<TransactionResultListener>? = null
        var userAgentKey: String? = null

        /**
         * Initiates the transaction process.
         * @param activity The activity instance from which transaction activity need's to open.
         * @param resultListener Listener to handle the transaction result.
         * @param paymentUrl URL for the payment process.
         */
        @JvmStatic
        fun initiateTransaction(
            activity: Activity,
            paymentUrl: String,
            referrer: String,
            resultListener: TransactionResultListener
        ) {

            transactionResultListener = WeakReference(resultListener)

            Intent(activity, TransactionActivity::class.java).apply {

                if (
                    userAgentKey == null ||
                    userAgentKey?.contains(Constants.REACT_NATIVE, true) == true
                ) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                putExtra(KEY_PAYMENT_URL, paymentUrl)
                putExtra(KEY_REFERRER, referrer)

                activity.startActivity(this)

            }

        }

        /**
         * To set TransactionResultListener callback.
         * @param listener TransactionResultListener.
         */
        internal fun setTransactionResultListener(listener: TransactionResultListener) {
            transactionResultListener = WeakReference(listener)
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_transaction)

        epayWebView = findViewById(R.id.epayWebView)
        progressBar = findViewById(R.id.progressBar)

        epayWebView.setBackgroundColor(Color.WHITE)

        transactionViewModel = ViewModelProvider(
            this,
            SavedStateViewModelFactory(application, this)
        )[TransactionViewModel::class.java]

        val color = ContextCompat.getColor(this, R.color.status_bar_color)
        CommonUtils.setStatusBarColor(window, color)

        referrer = intent.getStringExtra(KEY_REFERRER) ?: ""

        val paymentUrl = intent.getStringExtra(KEY_PAYMENT_URL)

        if (paymentUrl != null) {

            EPayValidation.validatePaymentUrlAndConnectivity(this, paymentUrl, referrer).let {
                if (it.valid.not()) {

                    transactionResultListener?.get()?.onCancelled(
                        PaymentResponseModel(
                            PAYMENT_STATUS_FAILED,
                            it.message,
                        )
                    )

                    finish()
                    return

                }
            }

            if (transactionResultListener != null) {
                setUserAgent()
                loadWebView(paymentUrl)
            }

        }

        transactionViewModel.jsThemeState.observe(this) { theme ->
            themeState = theme
        }

        transactionViewModel.selectedLanguage.observe(this) { lang ->
            selectedLanguage = lang
        }
    }

    /**
     * For setting the user agent platform name.
     */
    private fun setUserAgent() {

        val userAgent = userAgentKey ?: ""

        SecuredUserAgent().apply {
            setListener(this@TransactionActivity)
            call(userAgent)
        }

    }

    /**
     * For initializing SMS retriever and register broadcast receiver.
     */
    override fun initAutoReadSMS() {

        receivedOTP = ""
        initOtpReceiver = true

        SmsRetriever.getClient(this).also {

            //We can add user phone number or leave it blank
            it.startSmsUserConsent(null)
                .addOnSuccessListener {
                    // Do something when listener receives success.
                }
                .addOnFailureListener {
                    // Do something when listener receives failure.
                }
        }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            registerReceiver(
                smsVerificationReceiver, intentFilter, RECEIVER_EXPORTED
            )

        } else {

            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(
                smsVerificationReceiver, intentFilter
            )

        }

        lifecycleScope.launch(Dispatchers.Default) {

            val startTime = System.currentTimeMillis()
            val fiveMinutesInMillis = 5 * 60 * 1000L

            var isCompleted = false

            while ((System.currentTimeMillis() - startTime < fiveMinutesInMillis) && !isCompleted) {

                if (receivedOTP.isNotEmpty()) {

                    withContext(Dispatchers.Main) {

                        isCompleted = true

                        epayWebView.evaluateJavascript(
                            "javascript:autoReadOTP('$receivedOTP')", null
                        )

                    }

                }

                delay(500)

            }

        }

    }

    override fun onActivityFinish() {
        finish()
    }

    /**
     * For clean-up resources i.e unregister broadcast receiver.
     */
    override fun onDestroy() {

        if (initOtpReceiver) {
            initOtpReceiver = false
            unregisterReceiver(smsVerificationReceiver)
        }

        try {
            transactionResultListener?.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            if (this::epayWebView.isInitialized) {
                epayWebView.webChromeClient = null
                (epayWebView.parent as? ViewGroup)?.removeView(epayWebView)
                epayWebView.removeAllViews()
                epayWebView.stopLoading()
                epayWebView.clearHistory()
                epayWebView.clearCache(true)
                epayWebView.clearFormData()
                epayWebView.destroy()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()

    }

    /**
     * Handles Android back button presses according to JS-provided event model state.
     *
     * @param keyCode The integer code of the key pressed.
     * @param event The [KeyEvent] describing the key action.
     * @return True if the back press was handled, false otherwise.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {

            val jsEvent = transactionViewModel.jsEventState.value
            when (jsEvent?.currentMode) {
                Constants.HOME -> {
                    if (jsEvent.isBackEnabled) {
                        epayWebView.evaluateJavascript("javascript:onBackHandler('')", null)
                    } else {
                        showTransactionExitDialog()
                    }
                }

                Constants.UPI, Constants.CARD, Constants.NET_BANKING -> {
                    if (jsEvent.isBackEnabled) {
                        epayWebView.evaluateJavascript("javascript:onBackHandler('')", null)
                    } else {
                        if (epayWebView.canGoBack()) {
                            epayWebView.goBack()
                        } else {
                            Toast.makeText(this, "Web can't go back", Toast.LENGTH_SHORT).show()
                            showTransactionExitDialog()
                        }
                    }
                }

                else -> {}
            }

            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * For initializing EpayWebView and javascript interface.
     */
    @SuppressLint("JavascriptInterface")
    private fun loadWebView(loadUrl: String) {

        if (transactionResultListener != null) {

            epayWebView.addJavascriptInterface(
                EpayJsInterface(epayWebView, transactionResultListener, this),
                Constants.ANDROID
            )

        }

        epayWebView.initWebView(loadUrl, platform, referrer, lifecycleScope) {
            progressBar.visibility = View.GONE
        }

    }

    /**
     * ScListener callback function to set platform.
     * @param platformName name of the platform.
     */
    override fun call(platformName: String) {
        platform = platformName
    }

    /**
     * This dialog prevents for canceling the transaction is in progress.
     */
    private fun showTransactionExitDialog() {
        val themeModel = transactionViewModel.jsThemeState.value

        val language = transactionViewModel.selectedLanguage.value ?: "en"

        val themeColor =
            themeModel?.color ?: covertHexColorToString(this, R.color.default_theme_color)

        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.custum_dialog_transactionexit, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.show()
        val isTablet = this.resources.configuration.smallestScreenWidthDp >= 600
        val screenWidth = this.resources.displayMetrics.widthPixels

        val dialogWidth = if (isTablet) {
            (screenWidth * 0.55).toInt()
        } else {
            (screenWidth * 0.99).toInt()
        }
        dialog.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Get dialog components
        val continueButton = dialogView.findViewById<Button>(R.id.btnContinue)
        val cancelButton = dialogView.findViewById<Button>(R.id.btnCancel)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)

        FontHelper.loadFont(this, "name=Inter&weight=400") { tf ->
            dialogTitle.typeface = tf ?: Typeface.DEFAULT_BOLD
        }

        FontHelper.loadFont(this, "name=Inter&weight=500") { tf ->
            dialogMessage.typeface = tf ?: Typeface.SANS_SERIF
        }
        FontHelper.loadFont(this, "name=Inter&weight=500") { tf ->
            continueButton.typeface = tf ?: Typeface.SANS_SERIF
        }
        FontHelper.loadFont(this, "name=Inter&weight=500") { tf ->
            cancelButton.typeface = tf ?: Typeface.SANS_SERIF
        }

        // Set initial dialog text
        val localizedAlert = themeModel?.translations?.getAlert(language)
        dialogTitle.text = localizedAlert?.alertCaption ?: getString(R.string.are_you_sure)
        dialogMessage.text =
            localizedAlert?.alertTitle ?: getString(R.string.transaction_is_already_in_progress)
        continueButton.text = localizedAlert?.confirmLabel ?: getString(R.string.btntxt_continue)
        cancelButton.text = localizedAlert?.cancelLabel ?: getString(R.string.btntxt_cancel)

        cancelButton.elevation = 0f
        cancelButton.stateListAnimator = null
        cancelButton.setTextColor(Color.BLACK)

        try {
            // Set the initial button background and text color
            setButtonStyle(continueButton, themeColor)
        } catch (e: IllegalArgumentException) {
            setButtonStyle(
                continueButton,
                covertHexColorToString(this, R.color.default_theme_color)
            )
        }

        // Handle touch events for precise click detection
        var isClick = true
        var touchStartX = 0f
        var touchStartY = 0f
        continueButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val hexColor = covertHexColorToString(this, R.color.button_effect_color)

                    isClick = true  // Possible click
                    touchStartX = event.rawX
                    touchStartY = event.rawY
                    setButtonStyle(
                        continueButton,
                        hexColor
                    )
                }

                MotionEvent.ACTION_MOVE -> {
                    // Check if the finger moved significantly
                    val dx = Math.abs(event.rawX - touchStartX)
                    val dy = Math.abs(event.rawY - touchStartY)
                    if (dx > 20 || dy > 20) {  // Sensitivity threshold
                        isClick = false
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Reset to original color after release or cancel
                    setButtonStyle(continueButton, themeColor.toString())

                    // Only treat as a click if no sliding occurred
                    if (event.action == MotionEvent.ACTION_UP && isClick) {
                        val paymentResponse = PaymentResponseModel(
                            2,
                            message = themeModel?.message ?: getString(R.string.cancelled),
                            orderRefNumber = themeModel?.orderRefNumber,
                            sbiOrderRefNumber = themeModel?.sbiOrderRefNumber,
                            atrn = "",
                            totalAmount = themeModel?.totalAmount ?:""
                        )
                        transactionResultListener?.get()?.onCancelled(paymentResponse)
                        epayWebView.evaluateJavascript("javascript:onBackEvent()", null)
                        finish()
                        dialog.dismiss()
                    }
                }
            }
            true
        }
        // Handle cancel button click
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }


    /**
     * Sets the style for a button, including background color, border, and text color.
     * @param button The Button to apply the style to.
     * @param backgroundColor A String representing the background color in hex format (e.g., "#FF5733").
     */
    private fun setButtonStyle(button: Button, backgroundColor: String) {
        val borderColor = ContextCompat.getColor(this, R.color.dialog_border_button_color)
        val cornerRadius = 3f

        val backgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setStroke(5, borderColor)
            try {
                setColor(Color.parseColor(backgroundColor))

            } catch (e: IllegalArgumentException) {
                setColor(getColor(R.color.status_bar_color))
            }
            this.cornerRadius = resources.displayMetrics.density * cornerRadius
        }

        button.background = backgroundDrawable
        button.elevation = 0f
        button.stateListAnimator = null
        button.setTextColor(CommonUtils.getTextColor(backgroundColor))
    }

    override fun onJsBackEvent(json: String) {
        transactionViewModel.updateJsEventFromJson(json)
    }

    override fun onInitEvent(json: String) {
        transactionViewModel.updateThemeFromJson(json)
    }

    override fun onSetLanguage(json: String) {
        transactionViewModel.setLanguage(json)
    }

}