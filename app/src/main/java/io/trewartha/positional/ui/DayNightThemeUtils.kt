package io.trewartha.positional.ui

import android.content.Context
import android.content.res.Configuration


class DayNightThemeUtils(private val context: Context) {

    fun inDayMode() = !inNightMode()

    @Suppress("MemberVisibilityCanBePrivate")
    fun inNightMode() = getCurrentNightMode() == Configuration.UI_MODE_NIGHT_YES

    private fun getCurrentNightMode() = context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK
}