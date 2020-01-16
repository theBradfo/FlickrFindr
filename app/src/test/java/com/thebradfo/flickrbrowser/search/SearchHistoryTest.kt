package com.thebradfo.flickrbrowser.search

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks

/**
 * Unit tests for [SearchHistory].
 */
class SearchHistoryTest {

    private lateinit var searchHistory: SearchHistory

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setUp() {
        initMocks(this)

        doReturn(editor).whenever(sharedPreferences).edit()
        doReturn(editor).whenever(editor).putString(any(), any())

        searchHistory = spy(SearchHistory(sharedPreferences))
    }

    @Test
    fun searchHistory() {
        val dataList = "tom, brady, goat"
        doReturn(dataList).whenever(sharedPreferences).getString(SearchHistory.SEARCH_HISTORY_KEY, null)

        val result = searchHistory.searchHistory
        assertEquals(3, result.size)
        assertEquals(dataList.split(", "), result)
    }

    @Test
    fun `addSearchTerm first data`() {
        val newSearchTerm = "brady"

        doReturn(listOf<String>()).whenever(searchHistory).searchHistory

        searchHistory.addSearchTerm(newSearchTerm)

        verify(editor).putString(SearchHistory.SEARCH_HISTORY_KEY, newSearchTerm)
        verify(editor).apply()
    }

    @Test
    fun `addSearchTerm existing data`() {
        val newSearchTerm = "brady"
        val existingData = listOf("tom", "goat")

        doReturn(existingData).whenever(searchHistory).searchHistory

        searchHistory.addSearchTerm(newSearchTerm)

        val expectedData = existingData
            .toMutableList()
            .apply { add(0, newSearchTerm) }
            .joinToString()

        verify(editor).putString(SearchHistory.SEARCH_HISTORY_KEY, expectedData)
        verify(editor).apply()
    }

    @Test
    fun `addSearchTerm existing data duplicate`() {
        val newSearchTerm = "goat"
        val existingData = listOf("tom", "goat")

        doReturn(existingData).whenever(searchHistory).searchHistory

        searchHistory.addSearchTerm(newSearchTerm)

        verify(editor).putString(SearchHistory.SEARCH_HISTORY_KEY, "goat, tom")
        verify(editor).apply()
    }
}
