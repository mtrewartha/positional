package io.trewartha.positional.ui

import android.content.Context
import android.content.res.Configuration


class DayNightThemeUtils(private val context: Context) {

    fun inDayMode() = !inNightMode()

    fun inNightMode() = getCurrentNightMode() == Configuration.UI_MODE_NIGHT_YES

    private fun getCurrentNightMode() = context
            .getResources()
            .getConfiguration()
            .uiMode and Configuration.UI_MODE_NIGHT_MASK
}