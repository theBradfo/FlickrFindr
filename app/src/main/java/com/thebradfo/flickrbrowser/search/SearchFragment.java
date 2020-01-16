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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private View root;
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
        root = view;
        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setAdapter(groupAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof GridLayoutManager) {
                    final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                    presenter.pageScrolled(
                            gridLayoutManager.getChildCount(),
                            gridLayoutManager.findFirstVisibleItemPosition()
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
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
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
        Snackbar.make(root, R.string.search_error, Snackbar.LENGTH_SHORT).show();
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
        if (layoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) layoutManager)
                    .setSpanCount(requireContext().getResources().getInteger(R.integer.span_count));
        }
    }
}
