package com.mindfulness.googlenewsembed.interfaces

interface IResultListener {
    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String)
}