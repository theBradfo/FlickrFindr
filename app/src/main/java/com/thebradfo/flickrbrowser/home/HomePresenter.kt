package com.thebradfo.flickrbrowser.home

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.thebradfo.flickrbrowser.FlickrContentInteractor
import com.thebradfo.flickrbrowser.items.ImageItem
import com.thebradfo.flickrbrowser.models.Page
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import io.reactivex.Scheduler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

/**
 * MVP-style presenter for [HomeFragment].
 */
internal open class HomePresenter @Inject constructor(
    private val groupAdapter: GroupAdapter<ViewHolder>,
    private val homeView: HomeView,
    private val flickrContentInteractor: FlickrContentInteractor,
    @Named("MainScheduler") private val mainScheduler: Scheduler
): DefaultLifecycleObserver {

    /**
     * MVP-style view contract for the [HomePresenter].
     */
    interface HomeView {
        /**
         * Show the loading view.
         */
        fun showLoading()

        /**
         * Hide the loading view.
         */
        fun hideLoading()

        /**
         * Show the generic error presentation.
         */
        fun showError()

        /**
         * Open the image hosted at [imageUri] with backing [description].
         */
        fun openImage(imageUri: String, description: String?)
    }

    @VisibleForTesting
    val page: Page = Page(0, false)

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        loadItems(owner)
    }

    @VisibleForTesting
    open fun loadItems(owner: LifecycleOwner) {
        homeView.showLoading()

        flickrContentInteractor.getHomePhotos(page.pageNumber)
            .map { photos ->
                photos.map { ImageItem(it, homeView::openImage) }
            }
            .observeOn(mainScheduler)
            .doOnEvent { _, _ -> homeView.hideLoading()  }
            .autoDisposable(owner.scope())
            .subscribe(
                {
                    groupAdapter.addAll(it)
                },
                {
                    Timber.w(it, "Unable to fetch photos")
                    homeView.showError()
                }
            )
    }

    /**
     * A rudimentary paging mechanism for the calling view, given the [visibleItemCount],
     * [firstVisibleItemPosition] and provided [owner] to bind disposable events to.
     */
    fun pageScrolled(
        visibleItemCount: Int,
        firstVisibleItemPosition: Int,
        owner: LifecycleOwner
    ) {
        page.let {
            if (!it.isLoading && (visibleItemCount + firstVisibleItemPosition) >= groupAdapter.itemCount) {
                page.pageNumber++
                loadItems(owner)
            }
        }
    }
}
