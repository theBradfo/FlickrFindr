package com.thebradfo.flickrbrowser.home

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.*
import com.thebradfo.flickrbrowser.FlickrContentInteractor
import com.thebradfo.flickrbrowser.PhotoModelsUtil.getPhoto
import com.thebradfo.flickrbrowser.items.ImageItem
import com.uber.autodispose.android.AutoDisposeAndroidPlugins
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks
import org.mockito.verification.VerificationMode

/**
 * Unit tests for [HomePresenter].
 */
class HomePresenterTest {

    private lateinit var homePresenter: HomePresenter

    @Mock
    private lateinit var groupAdapter: GroupAdapter<ViewHolder>

    @Mock
    private lateinit var homeView: HomePresenter.HomeView

    @Mock
    private lateinit var flickrContentInteractor: FlickrContentInteractor

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

        homePresenter = spy(
            HomePresenter(
                groupAdapter,
                homeView,
                flickrContentInteractor,
                testScheduler
            )
        )

        AutoDisposeAndroidPlugins.setOnCheckMainThread { true }
    }

    @Test
    fun loadItems() {
        val data = listOf(
            getPhoto("0"),
            getPhoto("2")
        )

        val pageNumber = 2
        homePresenter.page.pageNumber = pageNumber

        doReturn(Single.just(data)).whenever(flickrContentInteractor).getHomePhotos(pageNumber)

        homePresenter.loadItems(owner)

        verify(homeView).showLoading()

        testScheduler.triggerActions()

        verify(flickrContentInteractor).getHomePhotos(pageNumber)
        verify(homeView).hideLoading()
        verify(homeView, never()).showError()

        verify(groupAdapter).addAll(data.map { ImageItem(it, homeView::openImage) })
    }

    @Test
    fun `load items with error`() {
        val pageNumber = 3
        homePresenter.page.pageNumber = pageNumber

        doReturn(Single.just(IllegalStateException("some error"))).whenever(flickrContentInteractor)
            .getHomePhotos(pageNumber)

        homePresenter.loadItems(owner)

        verify(homeView).showLoading()

        testScheduler.triggerActions()

        verify(flickrContentInteractor).getHomePhotos(pageNumber)
        verify(homeView).hideLoading()
        verify(homeView).showError()

        verify(groupAdapter, never()).addAll(any())
    }

    @Test
    fun onStart() {
        doNothing().whenever(homePresenter).loadItems(owner)

        homePresenter.onStart(owner)

        verify(homePresenter).loadItems(owner)
    }

    @Test
    fun `page scrolled not far enough, not loading`() =
        runScrollTest(false, 15, never())

    @Test
    fun `page scrolled enough, not loading`() =
        runScrollTest(false, 12, times(1))

    @Test
    fun `page scrolled enough, is loading`() =
        runScrollTest(true, 12, never())

    private fun runScrollTest(
        isLoading: Boolean,
        itemCount: Int = 12,
        loadItemsVerificationMode: VerificationMode
    ) {
        homePresenter.page.isLoading = isLoading
        doReturn(itemCount).whenever(groupAdapter).itemCount
        doNothing().whenever(homePresenter).loadItems(owner)

        homePresenter.pageScrolled(12, 0, owner)

        verify(homePresenter, loadItemsVerificationMode).loadItems(owner)
    }
}
