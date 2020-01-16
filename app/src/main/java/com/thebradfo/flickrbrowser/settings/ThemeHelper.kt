package com.thebradfo.flickrbrowser.settings

import androidx.appcompat.app.AppCompatDelegate

/**
 * A utility object that allows for overriding the system themes, where appropriate.
 */
object ThemeHelper {
    const val DARK_MODE_PREF_NAME = "DARK_MODE_ENABLED"

    @JvmStatic
    fun setDayNightTheme(isDarkTheme: Boolean) {

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
