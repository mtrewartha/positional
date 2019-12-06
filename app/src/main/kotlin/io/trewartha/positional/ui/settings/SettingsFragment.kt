package io.trewartha.positional.ui.settings

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.trewartha.positional.R


class SettingsFragment : PreferenceFragmentCompat() {

    private var rootKey: String? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        this.rootKey = rootKey
        preferenceManager.sharedPreferencesName = getString(R.string.settings_filename)
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return if (preference?.key == getString(R.string.settings_privacy_policy_key)) {
            showPrivacyPolicy()
            true
        } else {
            super.onPreferenceTreeClick(preference)
        }
    }

    override fun getCallbackFragment(): Fragment = this

    private fun showPrivacyPolicy() {
        val intent = Intent(ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
        startActivity(intent)
    }

    companion object {
        private const val PRIVACY_POLICY_URL =
                "https://github.com/miketrewartha/positional/blob/master/PRIVACY.md"
    }
}