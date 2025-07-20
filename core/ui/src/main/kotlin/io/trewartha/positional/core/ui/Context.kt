package io.trewartha.positional.core.ui

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity

val Context.activity: AppCompatActivity?
    get() {
        return when (this) {
            is AppCompatActivity -> this
            is ContextWrapper -> baseContext.activity
            else -> null
        }
    }
