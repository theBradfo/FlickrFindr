package com.thebradfo.flickrbrowser.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.thebradfo.flickrbrowser.R
import com.thebradfo.flickrbrowser.getFlickrBrowserPrefs
import kotlinx.android.synthetic.main.fragment_setting.*

/**
 * The fragment hosting all settings-related content.
 */
class SettingsFragment: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPrefs = requireContext().getFlickrBrowserPrefs()

        dark_mode_toggle.isChecked = sharedPrefs.getBoolean(ThemeHelper.DARK_MODE_PREF_NAME, false)

        dark_mode_toggle.setOnCheckedChangeListener { _, isChecked ->
            ThemeHelper.setDayNightTheme(isChecked)

            sharedPrefs.edit()
                .putBoolean(ThemeHelper.DARK_MODE_PREF_NAME, isChecked)
                .apply()
        }
    }

    companion object {
        const val TAG = "SettingsFragment"
    }
}
