package com.mindfulness.googlenewsembed.utils

import android.content.Context
import android.content.Intent
import com.mindfulness.googlenewsembed.ui.main.MainActivity

class ViewUtils {
    companion object {
        const val API_KEY="04e1b009841d4d8e994bd9057527f788"
    }
}

fun Context.startHomeActivity() =
    Intent(this, MainActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
