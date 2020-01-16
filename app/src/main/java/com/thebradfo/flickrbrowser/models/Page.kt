package com.thebradfo.flickrbrowser.models

/**
 * A data model representing the current [pageNumber] and [isLoading] state for the UI and network.
 */
data class Page(
    var pageNumber: Int,
    var isLoading: Boolean
)
