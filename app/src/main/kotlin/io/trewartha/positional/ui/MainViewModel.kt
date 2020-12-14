package io.trewartha.positional.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import io.trewartha.positional.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ProducerScope
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class MainViewModel(app: Application) : AndroidViewModel(app) {

//    val theme: LiveData<Int> by lazy {
//        callbackFlow {
//            if (prefs.contains(prefsKeyTheme))
//                offer(prefs.getString(prefsKeyTheme, null))
//            prefThemeListener = PrefThemeListener(this)
//            prefs.registerOnSharedPreferenceChangeListener(prefThemeListener)
//            awaitClose {
//                prefThemeListener?.let {
//                    prefs.unregisterOnSharedPreferenceChangeListener(it)
//                }
//            }
//        }.map { prefString ->
//            when (prefString?.toUpperCase(Locale.ROOT)?.let { it1 -> Theme.valueOf(it1) }) {
//                Theme.DARK -> R.style.Positional_Dark
//                Theme.LIGHT -> R.style.Positional_Light
//                Theme.SYSTEM, null -> R.style.Positional_System
//            }
//        }.asLiveData()
//    }

    val theme: Int by lazy {
        val themePref = prefs.getString(prefsKeyTheme, null)
                ?.toUpperCase(Locale.ROOT)
                ?.let { Theme.valueOf(it) }
        when (themePref) {
            Theme.DARK -> R.style.Positional_Dark
            Theme.LIGHT -> R.style.Positional_Light
            Theme.SYSTEM, null -> R.style.Positional_System
        }
    }

    private val prefsKeyTheme = app.getString(R.string.settings_theme_key)
    private val prefs = app.getSharedPreferences(
            app.getString(R.string.settings_filename),
            Context.MODE_PRIVATE
    )
    private var prefThemeListener: PrefThemeListener? = null

    private inner class PrefThemeListener(
            val producerScope: ProducerScope<String?>
    ) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
            if (key == prefsKeyTheme)
                producerScope.offer(sharedPrefs.getString(key, null))
        }
    }
}