package com.thebradfo.flickrbrowser

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.thebradfo.flickrbrowser.home.HomeFragment
import com.thebradfo.flickrbrowser.search.SearchFragment
import com.thebradfo.flickrbrowser.settings.SettingsFragment
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * The main activity host of this single activity style application.
 */
class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_host, HomeFragment(), HomeFragment.TAG)
                .commit()

            fab.setOnClickListener {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_host, SearchFragment())
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }

        setSupportActionBar(bottom_app_bar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottomappbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_settings -> {
                SettingsFragment().showNow(supportFragmentManager, SettingsFragment.TAG)
            }
        }

        return true
    }
}
