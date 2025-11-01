package com.epay.sbi.android.model

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
 * Data class representing the event received from JavaScript for back handling and other controls.
 *
 * @property status The status code of the event (e.g., 1 = active, 0 = inactive).
 * @property currentMode Indicates the mode of the WebView (e.g., -1 = native, 0/1/2 = different Web modes).
 * @property isBackEnabled Whether back navigation is allowed from JavaScript's perspective.
 * @property key A unique identifier or key for the event, if needed.
 */


data class EventModel(
    val status: Int,
    val currentMode: Int,
    val isBackEnabled: Boolean,
    val key: String
) {
    companion object {
        /**
         * Parses a JSON string into a [JsEventModel] object.
         *
         * @param jsonString The JSON string received from JavaScript.
         * @return A [JsEventModel] object if parsing succeeds, or null if it fails.
         */
        fun parseJson(jsonString: String): EventModel? {
            return try {
                val json = JSONObject(jsonString)
                EventModel(
                    status = json.optInt("status", -1),
                    currentMode = json.optInt("currentMode", -1),
                    isBackEnabled = json.optBoolean("isBackEnabled", false),
                    key = json.optString("key", "")
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
