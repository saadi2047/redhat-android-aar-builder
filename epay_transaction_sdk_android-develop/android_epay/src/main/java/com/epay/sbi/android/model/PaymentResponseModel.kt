package com.epay.sbi.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

/*
 *
 * Copyright (c) [2024] [State Bank of India]
 * All rights reserved.
 *
 * Author:@V1017516(RiteshShukla)
 * Version:1.0
 *
 */

/**
 * Used for handling the payment response.
 * @param status whether the payment is success, failed or cancelled.
 * @param message transaction related to message as per the status.
 * @param orderRefNumber transaction order id.
 * @param sbiOrderRefNumber transaction order number.
 * @param atrn transaction number.
 * @param totalAmount transaction amt.
 */

@Parcelize
data class PaymentResponseModel(
    val status: Int,
    val message: String,
    val orderRefNumber: String? = "",
    val sbiOrderRefNumber: String? = "",
    val atrn: String? = "",
    val totalAmount: String? = ""
) : Parcelable {

    companion object {

        fun parseJson(jsonString: String): PaymentResponseModel {

            val jsonObject = JSONObject(jsonString)

            return PaymentResponseModel(
                jsonObject.optInt("status"),
                jsonObject.optString("message"),
                jsonObject.optString("orderRefNumber"),
                jsonObject.optString("sbiOrderRefNumber"),
                jsonObject.optString("atrn"),
                jsonObject.optString("totalAmount", "")
            )

        }

    }

}
