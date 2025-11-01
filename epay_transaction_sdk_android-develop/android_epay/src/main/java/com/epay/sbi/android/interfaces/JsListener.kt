package com.epay.sbi.android.interfaces

interface JsListener {

    fun onJsBackEvent(json: String)
    fun onInitEvent(json: String)
    fun onSetLanguage(json: String)
    fun initAutoReadSMS()
    fun onActivityFinish()

}