package com.epay.sbi.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

/*
 *
 * Copyright (c) [2024] [State Bank of India]
 * All rights reserved.
 *
 * Author:@V1017566(Palash Gour)
 * Version:1.0
 *
 */

/**
 * A data class that represents a theme model with a status and a color.
 * @property status The status code representing the theme state.
 * @property color The color value in string format (e.g., hex code).
 */

@Parcelize
data class ThemeModel(
    val status: Int,
    val color: String,
    val sbiOrderRefNumber: String,
    val orderRefNumber: String,
    val totalAmount: String? = "",
    val message: String?,
    val translations: Translations

) : Parcelable {
    companion object {
        fun parseJson(jsonString: String): ThemeModel? {
            return try {
                val json = JSONObject(jsonString)
                val status = json.optInt("status", -1)
                val color = json.optString("color", "#000000")
                val sbiOrderRefNumber = json.optString("sbiOrderRefNumber", "")
                val orderRefNumber = json.optString("orderRefNumber", "")
                val totalAmount = json.optString("totalAmount", "")
                val message = json.optString("message", "")

                val translationsJson = json.optJSONObject("translations")
                val map = mutableMapOf<String, LanguageStrings?>()

                translationsJson?.keys()?.forEach { lang ->
                    translationsJson.optJSONObject(lang)?.let { langJson ->
                        map[lang] = LanguageStrings.fromJson(langJson)
                    }
                }

                ThemeModel(
                    status = status,
                    color = color,
                    orderRefNumber=orderRefNumber,
                    sbiOrderRefNumber =sbiOrderRefNumber,
                    totalAmount=totalAmount,
                    message = message,
                    translations = Translations(map)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

@Parcelize
data class Translations(
    // Bundles/maps with parcelable values are supported
    val map: Map<String, LanguageStrings?>
) : Parcelable {
    fun getAlert(language: String): AlertData? {
        return map[language.lowercase()]?.alert
            ?: map["en"]?.alert // fallback to English
    }
}

@Parcelize
data class LanguageStrings(
    val alert: AlertData?,
    val upi: UpiData?
) : Parcelable {
    companion object {
        fun fromJson(json: JSONObject): LanguageStrings {
            return LanguageStrings(
                alert = json.optJSONObject("alert")?.let {
                    AlertData(
                        it.optString("alertCaption", "Default Caption"),
                        it.optString("alertTitle",   "Default Title"),
                        it.optString("cancelLabel",  "Cancel"),
                        it.optString("confirmLabel", "Continue")
                    )
                },
                upi = json.optJSONObject("upi")?.let {
                    UpiData(
                        it.optString("upiCaption",    "Default UPI Caption"),
                        it.optString("upiTitle",      "Default UPI Title"),
                        it.optString("confirmLabel",  "Continue")
                    )
                }
            )
        }
    }
}

@Parcelize
data class AlertData(
    val alertCaption: String,
    val alertTitle: String,
    val cancelLabel: String,
    val confirmLabel: String
) : Parcelable

@Parcelize
data class UpiData(
    val upiCaption: String,
    val upiTitle: String,
    val confirmLabel: String
) : Parcelable
