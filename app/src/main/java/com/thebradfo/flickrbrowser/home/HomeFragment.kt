package com.thebradfo.flickrbrowser.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_NONE
import com.google.android.material.snackbar.Snackbar
import com.thebradfo.flickrbrowser.ImageDialogFragment
import com.thebradfo.flickrbrowser.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

/**
 * The fragment displayed by default in the main host activity.
 */
class HomeFragment: DaggerFragment(), HomePresenter.HomeView {

    @Inject
    internal lateinit var groupAdapter: GroupAdapter<ViewHolder>

    @Inject
    internal lateinit var presenter: HomePresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.layoutManager = StaggeredGridLayoutManager(
            requireContext().resources.getInteger(R.integer.span_count),
            StaggeredGridLayoutManager.VERTICAL
        ).apply {
            // the images are not of a fixed size, so let's reduce the item shuffling by abandoning
            //  this layoutmanager's gap strategy.
            gapStrategy = GAP_HANDLING_NONE
        }

        recycler_view.adapter = groupAdapter
        recycler_view.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView.layoutManager?.let { layoutManager ->
                    if (layoutManager is StaggeredGridLayoutManager) {
                        presenter.pageScrolled(
                            layoutManager.childCount,
                            layoutManager.findFirstVisibleItemPositions(null).first(),
                            this@HomeFragment
                        )
                    }
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(presenter)
    }

    companion object {
        const val TAG = "HomeFragment"
    }

    override fun showLoading() {
        progress.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress.visibility = View.GONE
    }

    override fun showError() {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.home_error,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun openImage(imageUri: String, description: String?) {
        ImageDialogFragment.openFragment(childFragmentManager, imageUri, description)
    }
}
