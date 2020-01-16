package com.thebradfo.flickrbrowser

import com.nhaarman.mockitokotlin2.*
import com.thebradfo.flickrbrowser.PhotoModelsUtil.getPhoto
import com.thebradfo.flickrbrowser.PhotoModelsUtil.getPhotoResponse
import com.thebradfo.flickrbrowser.network.FlickrService
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks

/**
 * Unit tests for [FlickrContentInteractor].
 */
class FlickrContentInteractorTest {

    private lateinit var flickrContentInteractor: FlickrContentInteractor
    private lateinit var testScheduler: TestScheduler

    @Mock
    private lateinit var flickrService: FlickrService

    @Before
    fun setUp() {
        initMocks(this)

        testScheduler = TestScheduler()
        flickrContentInteractor = FlickrContentInteractor(flickrService, API_KEY, testScheduler)
    }

    @Test
    fun getHomePhotos() {
        val page = 3
        flickrContentInteractor = spy(flickrContentInteractor)

        doReturn(Single.just(getPhotoResponse(emptyList()))).whenever(flickrContentInteractor).getPhotos(any(), any())

        flickrContentInteractor.getHomePhotos(page)

        verify(flickrContentInteractor).getPhotos(
            FlickrContentInteractor.DEFAULT_SEARCH_TERM,
            page
        )
    }

    @Test
    fun getPhotos() {
        val searchTerm = "6 rings"
        val page = 2

        val photos = listOf(
            getPhoto("0"),
            getPhoto("1")
        )

        doReturn(Single.just(getPhotoResponse(photos))).whenever(flickrService).getPhotos(
            API_KEY,
            searchTerm,
            FlickrContentInteractor.COUNT,
            page)

        val testStream = flickrContentInteractor.getPhotos(searchTerm, page).test()

        testScheduler.triggerActions()

        testStream.assertComplete()
        testStream.assertValue(photos)

        verify(flickrService).getPhotos(
            API_KEY,
            searchTerm,
            FlickrContentInteractor.COUNT,
            page
        )
    }

    private companion object {
        const val API_KEY = "tom-brady-the-goat"
    }
}
