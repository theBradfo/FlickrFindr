package com.thebradfo.flickrbrowser.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.thebradfo.flickrbrowser.ImageDialogFragment;
import com.thebradfo.flickrbrowser.R;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.ViewHolder;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

/**
 * The SearchFragment used to host search results from the user.
 */
public class SearchFragment extends DaggerFragment implements SearchPresenter.SearchView {

    @Inject
    SearchPresenter presenter;

    @Inject
    GroupAdapter<ViewHolder> groupAdapter;

    private ProgressBar progressBar;
    private View emptyState;
    private EditText searchTextView;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress);
        emptyState = view.findViewById(R.id.empty_state);
        recyclerView = view.findViewById(R.id.recycler_view);


        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                requireContext().getResources().getInteger(R.integer.span_count),
                StaggeredGridLayoutManager.VERTICAL);
        // the images are not of a fixed size, so let's reduce the item shuffling by abandoning
        //  this layoutmanager's gap strategy.
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        // default the span count to 0 since we have show search results initially
        layoutManager.setSpanCount(1);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(groupAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof StaggeredGridLayoutManager) {
                    final StaggeredGridLayoutManager gridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                    presenter.pageScrolled(
                            gridLayoutManager.getChildCount(),
                            gridLayoutManager.findFirstVisibleItemPositions(null)[0]
                    );
                }
            }
        });

        searchTextView = view.findViewById(R.id.search_text_view);
        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                presenter.textEntered(editable.toString());
            }
        });

        getLifecycle().addObserver(presenter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLifecycle().addObserver(presenter);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyState() {
        emptyState.setVisibility(View.GONE);
    }

    @Override
    public void showError() {
        Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.search_error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void openImage(@NotNull String imageUri, @Nullable String description) {
        ImageDialogFragment.openFragment(getChildFragmentManager(), imageUri, description);
    }

    @Override
    public void setSearchText(@NotNull String searchTerm) {
        searchTextView.setText(searchTerm);
    }

    @Override
    public void resetColumnCount() {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            ((StaggeredGridLayoutManager) layoutManager)
                    .setSpanCount(requireContext().getResources().getInteger(R.integer.span_count));
        }
    }
}
