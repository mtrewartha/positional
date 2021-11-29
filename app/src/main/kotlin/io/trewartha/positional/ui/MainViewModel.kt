package io.trewartha.positional.ui

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    app: Application,
    private val prefs: SharedPreferences
) : AndroidViewModel(app) {

    val theme: LiveData<Theme> by lazy {
        callbackFlow {
            if (prefs.contains(prefsKeyTheme))
                trySend(prefs.getString(prefsKeyTheme, null))
            prefThemeListener = PrefThemeListener(this)
            prefs.registerOnSharedPreferenceChangeListener(prefThemeListener)
            awaitClose {
                prefThemeListener?.let { prefs.unregisterOnSharedPreferenceChangeListener(it) }
            }
        }.map { prefString ->
            prefString?.uppercase()?.let { it -> Theme.valueOf(it) } ?: Theme.SYSTEM
        }.asLiveData()
    }

    private val prefsKeyTheme = app.getString(R.string.settings_theme_key)
    private var prefThemeListener: PrefThemeListener? = null

    private inner class PrefThemeListener(
        val producerScope: ProducerScope<String?>
    ) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
            if (key == prefsKeyTheme)
                producerScope.trySend(sharedPrefs.getString(key, null))
        }
    }
}