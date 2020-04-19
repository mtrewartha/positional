package io.trewartha.positional.common

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData

class SharedPrefsBooleanLiveData(
        private val sharedPrefs: SharedPreferences,
        private val key: String,
        private val defaultValue: Boolean
) : MediatorLiveData<Boolean>() {

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
        if (changedKey == key) readAndSetValue()
    }

    init {
        readAndSetValue()
    }

    override fun onActive() {
        super.onActive()
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun readAndSetValue() {
        value = try {
            sharedPrefs.getBoolean(key, defaultValue)
        } catch (exception: Exception) {
            defaultValue
        }
    }
}