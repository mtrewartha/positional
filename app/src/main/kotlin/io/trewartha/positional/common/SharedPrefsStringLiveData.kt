package io.trewartha.positional.common

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData

class SharedPrefsStringLiveData(
        private val sharedPrefs: SharedPreferences,
        private val key: String,
        private val defaultValue: String
) : MediatorLiveData<String>() {

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
        if (changedKey == key) readAndSetValue()
    }

    init {
        readAndSetValue()
    }

    override fun onActive() {
        super.onActive()
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
        readAndSetValueIfChanged()
    }

    override fun onInactive() {
        super.onInactive()
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun readAndSetValue() {
        value = readValue()
    }

    private fun readAndSetValueIfChanged() {
        val readValue = readValue()
        if (value != readValue) value = readValue
    }

    private fun readValue(): String? {
        return try {
            sharedPrefs.getString(key, defaultValue)
        } catch (exception: Exception) {
            defaultValue
        }
    }
}