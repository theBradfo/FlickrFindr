package com.thebradfo.flickrbrowser.search

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.thebradfo.flickrbrowser.FlickrContentInteractor
import com.thebradfo.flickrbrowser.items.ImageItem
import com.thebradfo.flickrbrowser.items.SearchItem
import com.thebradfo.flickrbrowser.models.Page
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

/**
 * MVP-style presenter used for the [SearchFragment] feature.
 */
internal class SearchPresenter @Inject constructor(
    private val searchView: SearchView,
    private val flickrContentInteractor: FlickrContentInteractor,
    private val groupAdapter: GroupAdapter<ViewHolder>,
    private val searchHistory: SearchHistory,
    @Named("MainScheduler") private val mainScheduler: Scheduler,
    @Named("IoScheduler") private val ioScheduler: Scheduler
) : DefaultLifecycleObserver {

    /**
     * The MVP-style view contract for the [SearchPresenter].
     */
    interface SearchView {
        fun showProgress()

        fun hideProgress()

        fun showEmptyState()

        fun hideEmptyState()

        fun showError()

        fun openImage(imageUri: String, description: String?)

        fun setSearchText(searchTerm: String)

        fun resetColumnCount()
    }

    // disposable that allows us to cancel any pending search requests
    @VisibleForTesting
    var disposable: Disposable? = null

    @VisibleForTesting
    var currentSearchTerm: String? = null

    @VisibleForTesting
    val page: Page = Page(0, false)

    override fun onStart(owner: LifecycleOwner) {
        groupAdapter.addAll(
            searchHistory.searchHistory
                .map {
                    SearchItem(it) { searchTerm ->
                        groupAdapter.clear()
                        searchView.resetColumnCount()
                        searchView.setSearchText(searchTerm)
                    }
                }
        )
    }

    fun textEntered(text: String) {
        if (text.isNotBlank()) {
            disposable?.dispose()

            if (currentSearchTerm == null) {
                searchView.resetColumnCount()
                groupAdapter.clear()
            }

            currentSearchTerm = text

            disposable = flickrContentInteractor.getPhotos(text, page.pageNumber)
                .observeOn(mainScheduler)
                .doOnSubscribe { searchView.showProgress() }
                .observeOn(ioScheduler)
                .map { photos ->
                    photos.map { ImageItem(it, searchView::openImage) }
                }
                .observeOn(mainScheduler)
                .doOnEvent { _, _ ->
                    searchView.hideEmptyState()
                    searchView.hideProgress()
                }
                .subscribe(
                    {
                        groupAdapter.addAll(it)

                        if (it.isEmpty() && groupAdapter.itemCount == 0) {
                            searchView.showEmptyState()
                        } else {
                            searchView.hideEmptyState()
                        }
                    },
                    {
                        Timber.w(it, "Unable to fetch photos")
                        searchView.showError()

                        if (groupAdapter.itemCount == 0) {
                            searchView.showEmptyState()
                        }
                    }
                )
        }
    }

    fun pageScrolled(
        visibleItemCount: Int,
        firstVisibleItemPosition: Int
    ) {
        currentSearchTerm?.let { searchTerm ->
            page.let {
                if (!it.isLoading && (visibleItemCount + firstVisibleItemPosition) >= groupAdapter.itemCount) {
                    page.pageNumber++
                    textEntered(searchTerm)
                }
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        disposable?.dispose()

        currentSearchTerm?.let { searchHistory.addSearchTerm(it) }
    }
}
