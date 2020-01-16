package com.thebradfo.flickrbrowser

import android.content.Context
import android.content.SharedPreferences

const val PREFS_FILE = "FlickrSettings"

/**
 * Provides [SharedPreferences] scoped to this application.
 */
fun Context.getFlickrBrowserPrefs(): SharedPreferences =
    getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
