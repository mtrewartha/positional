package io.trewartha.positional.common

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData

class SharedPreferenceStringLiveData(
        private val sharedPreferences: SharedPreferences,
        private val key: String,
        private val defaultValue: String
) : MediatorLiveData<String>() {

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
        if (changedKey == key) readAndSetValue()
    }

    override fun onActive() {
        super.onActive()
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        readAndSetValue()
    }

    override fun onInactive() {
        super.onInactive()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun readAndSetValue() {
        value = sharedPreferences.getString(key, defaultValue)
    }
}