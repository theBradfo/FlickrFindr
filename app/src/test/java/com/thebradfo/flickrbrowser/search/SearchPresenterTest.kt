package com.thebradfo.flickrbrowser.search

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.*
import com.thebradfo.flickrbrowser.FlickrContentInteractor
import com.thebradfo.flickrbrowser.PhotoModelsUtil
import com.thebradfo.flickrbrowser.items.ImageItem
import com.thebradfo.flickrbrowser.items.SearchItem
import com.thebradfo.flickrbrowser.models.Photo
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks
import org.mockito.verification.VerificationMode

/**
 * Unit tests for [SearchPresenter].
 */
class SearchPresenterTest {

    private lateinit var searchPresenter: SearchPresenter

    @Mock
    private lateinit var searchView: SearchPresenter.SearchView

    @Mock
    private lateinit var flickrContentInteractor: FlickrContentInteractor

    @Mock
    private lateinit var groupAdapter: GroupAdapter<ViewHolder>

    @Mock
    private lateinit var searchHistory: SearchHistory

    @Mock
    private lateinit var owner: LifecycleOwner

    @Mock
    private lateinit var lifecycle: Lifecycle

    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUp() {
        initMocks(this)

        testScheduler = TestScheduler()

        doReturn(lifecycle).whenever(owner).lifecycle
        doReturn(Lifecycle.State.RESUMED).whenever(lifecycle).currentState

        searchPresenter = SearchPresenter(
            searchView,
            flickrContentInteractor,
            groupAdapter,
            searchHistory,
            testScheduler,
            testScheduler
        )
    }

    @Test
    fun onStop() {
        val disposable: Disposable = mock()
        searchPresenter.disposable = disposable

        val searchTerm = "tom brady is the GOAT"
        searchPresenter.currentSearchTerm = searchTerm

        searchPresenter.onStop(owner)

        verify(disposable).dispose()
        verify(searchHistory).addSearchTerm(searchTerm)
    }

    @Test
    fun onStart() {
        val searchTerms = listOf("tom", "brady", "is", "the", "greatest")
        doReturn(searchTerms).whenever(searchHistory).searchHistory

        searchPresenter.onStart(owner)

        val captor = argumentCaptor<List<SearchItem>>()
        verify(groupAdapter).addAll(captor.capture())

        assertEquals(searchTerms.size, captor.firstValue.size)

        captor.firstValue.forEachIndexed { index, item ->
            assertEquals(searchTerms[index], item.searchTerm)
        }
    }

    @Test
    fun `textEntered initial state`() =
        assertTextEntered("tom brady 2", never(), never())

    @Test
    fun `textEntered secondary state`() =
        assertTextEntered(null)

    private fun assertTextEntered(
        initialSearchTerm: String? = "tom brady",
        resetColumnCountVerificationMode: VerificationMode = times(1),
        groupAdapterClearVerificationMode: VerificationMode = times(1),
        errorVerificationMode: VerificationMode = never(),
        emptyStateVerificationMode: VerificationMode = never()
    ) {
        val disposable: Disposable = mock()
        searchPresenter.disposable = disposable
        searchPresenter.currentSearchTerm = initialSearchTerm

        val updatedText = "brady search"

        val data = listOf(
            PhotoModelsUtil.getPhoto("0"),
            PhotoModelsUtil.getPhoto("2")
        )

        doReturn(Single.just(data)).whenever(flickrContentInteractor).getPhotos(anyString(), anyInt())

        searchPresenter.textEntered(updatedText)

        verify(disposable).dispose()
        verify(searchView, resetColumnCountVerificationMode).resetColumnCount()
        verify(groupAdapter, groupAdapterClearVerificationMode).clear()

        testScheduler.triggerActions()

        verify(searchView).showLoading()
        verify(searchView).hideLoading()
        verify(searchView, errorVerificationMode).showError()
        verify(searchView, emptyStateVerificationMode).showEmptyState()

        verify(groupAdapter).update(data.map { ImageItem(it, searchView::openImage) })

        assertEquals(updatedText, searchPresenter.currentSearchTerm)
    }

    @Test
    fun `textEntered with error`() {
        val disposable: Disposable = mock()
        searchPresenter.disposable = disposable

        doReturn(Single.error<List<Photo>>(IllegalStateException("wtf")))
            .whenever(flickrContentInteractor).getPhotos(anyString(), anyInt())

        searchPresenter.textEntered("updated")

        testScheduler.triggerActions()

        verify(searchView).showLoading()
        verify(searchView).hideLoading()
        verify(searchView).hideEmptyState()
        verify(searchView).showError()
        verify(searchView).showEmptyState()

        verify(groupAdapter, never()).addAll(any())
    }

    @Test
    fun `textEntered with empty state`() {
        val disposable: Disposable = mock()
        searchPresenter.disposable = disposable

        doReturn(Single.just(emptyList<Photo>())).whenever(flickrContentInteractor).getPhotos(anyString(), anyInt())

        searchPresenter.textEntered("updated")

        testScheduler.triggerActions()

        verify(searchView).showLoading()
        verify(searchView).hideLoading()
        verify(searchView).hideEmptyState()
        verify(searchView, never()).showError()
        verify(searchView).showEmptyState()

        verify(groupAdapter).update(emptyList())
    }
}
