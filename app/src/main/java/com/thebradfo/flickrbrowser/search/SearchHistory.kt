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

    /**
     * Provide the [List] of search strings previously entered by the user, or an empty list
     * if none.  This api will always return a maximum of [MAX_SEARCH_HISTORY] items.
     */
    open val searchHistory: List<String>
        get() = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
            ?.split(DELIMITER)
            ?: emptyList()

    /**
     * Adds a [searchTerm] to the [searchHistory] storage. If this string exists previously in the
     * history, it is removed and added to the top of the stack.
     */
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
