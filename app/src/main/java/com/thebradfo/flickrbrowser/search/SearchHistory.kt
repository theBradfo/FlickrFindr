package com.thebradfo.flickrbrowser.search

import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A class to abstract away the backing data store for the previously used search terms.
 */
@Singleton
open class SearchHistory @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    open val searchHistory: List<String>
        get() = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
            ?.split(DELIMITER)
            ?: emptyList()

    open fun addSearchTerm(searchTerm: String) {
        if (searchTerm.isNotBlank()) {
            val searchStack = searchHistory
                .toMutableList()
                .apply {
                    remove(searchTerm)
                    add(0, searchTerm)
                }
                .take(MAX_SEARCH_HISTORY)

            sharedPreferences.edit()
                .putString(SEARCH_HISTORY_KEY, searchStack.joinToString())
                .apply()
        }
    }

    companion object {

        @VisibleForTesting
        const val SEARCH_HISTORY_KEY = "search_history"

        private const val DELIMITER = ", "
        private const val MAX_SEARCH_HISTORY = 10
    }
}
