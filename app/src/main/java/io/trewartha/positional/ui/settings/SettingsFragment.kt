package io.trewartha.positional.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import io.trewartha.positional.R


class SettingsFragment : PreferenceFragmentCompat() {

    private var rootKey: String? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        this.rootKey = rootKey
        preferenceManager.sharedPreferencesName = getString(R.string.settings_filename)
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun getCallbackFragment(): Fragment = this
}