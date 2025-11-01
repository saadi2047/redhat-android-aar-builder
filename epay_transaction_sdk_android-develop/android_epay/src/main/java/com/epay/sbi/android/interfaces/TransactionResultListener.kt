package com.epay.sbi.android.interfaces

import com.epay.sbi.android.model.PaymentResponseModel

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
 * Interface that will be implemented by the merchant app.
 * Payment result will be passed to the merchant using these methods.
 */
interface TransactionResultListener {

    /**
     * Method used for passing success response to the merchant app.
     * @param paymentResponseModel response containing all the required details regarding transaction.
     */
    fun onSuccess(paymentResponseModel: PaymentResponseModel)

    /**
     * Method used for passing failed response to the merchant app.
     * @param paymentResponseModel response containing all the required details regarding transaction.
     */
    fun onFailed(paymentResponseModel: PaymentResponseModel)

    /**
     * Method used for passing cancelled response to the merchant app.
     * @param paymentResponseModel response containing all the required details regarding transaction.
     */
    fun onCancelled(paymentResponseModel: PaymentResponseModel)

    /**
     * Method used for passing timeout response to the merchant app.
     * @param paymentResponseModel response containing all the required details regarding transaction.
     */
    fun onTimeOut(paymentResponseModel: PaymentResponseModel)

    /**
     * Method used for showing error occurred while loading the page.
     * @param paymentResponseModel response containing all the required details regarding transaction.
     */
    fun onError(paymentResponseModel: PaymentResponseModel)

    /**
     * Method used for showing error occurred when the transaction is in pending status.
     * @param paymentResponseModel response containing all the required details regarding transaction.
     */
    fun onPending(paymentResponseModel: PaymentResponseModel)

}