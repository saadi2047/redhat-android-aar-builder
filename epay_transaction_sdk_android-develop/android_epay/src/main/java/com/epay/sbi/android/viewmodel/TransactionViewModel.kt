package com.epay.sbi.android.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epay.sbi.android.model.EventModel
import com.epay.sbi.android.model.ThemeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
 * MainViewModel is responsible for managing UI state related to:
 * - Language selection
 * - Theme configuration from JavaScript
 * - Event data from JavaScript
 *
 * @param savedStateHandle A handle to saved state passed from the system.
 */
class TransactionViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME_JSON = "themeJson"
        private const val KEY_EVENT_JSON = "eventJson"
    }

    /** LiveData for the currently selected language, default is "en" */
    val selectedLanguage: LiveData<String> = savedStateHandle.getLiveData(KEY_LANGUAGE, "en")

    /** LiveData for parsed theme model */
    private val _jsThemeState = MutableLiveData<ThemeModel?>()
    val jsThemeState: LiveData<ThemeModel?> = _jsThemeState

    /** LiveData for parsed JS event model */
    private val _jsEventState = MutableLiveData<EventModel?>()
    val jsEventState: LiveData<EventModel?> = _jsEventState

    init {
        // Restore saved theme state if present
        savedStateHandle.get<String>(KEY_THEME_JSON)?.let { themeJson ->
            _jsThemeState.value = ThemeModel.parseJson(themeJson)
        }

        // Restore saved event state if present
        savedStateHandle.get<String>(KEY_EVENT_JSON)?.let { eventJson ->
            _jsEventState.value = EventModel.parseJson(eventJson)
        }
    }

    /**
     * Updates the selected language in saved state.
     *
     * @param language The language code (e.g., "en", "hi")
     */
    fun setLanguage(language: String) {
        viewModelScope.launch(Dispatchers.Main) {
            savedStateHandle[KEY_LANGUAGE] = language
        }
    }

    /**
     * Updates the theme configuration by parsing JSON.
     *
     * @param json A JSON string containing theme details.
     */
    fun updateThemeFromJson(json: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                savedStateHandle[KEY_THEME_JSON] = json
                val parsedTheme = ThemeModel.parseJson(json)
                _jsThemeState.value = parsedTheme
            } catch (e: Exception) {
                _jsThemeState.value = null
                Log.e("MainViewModel", "Error parsing theme JSON", e)
            }
        }
    }

    /**
     * Updates the event data from JavaScript by parsing JSON.
     *
     * @param json A JSON string representing an event.
     */
    fun updateJsEventFromJson(json: String) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                savedStateHandle[KEY_EVENT_JSON] = json
                val jsEvent = EventModel.parseJson(json)
                _jsEventState.value = jsEvent
            } catch (e: Exception) {
                _jsEventState.value = null
                Log.e("MainViewModel", "Failed to parse JS event JSON", e)
            }
        }
    }

    /**
     * Sets the JS event directly (used in testing or local UI updates).
     *
     * @param event A JsEventModel instance or null to clear.
     */
    fun updateJsEvent(event: EventModel?) {
        _jsEventState.value = event
    }
}
